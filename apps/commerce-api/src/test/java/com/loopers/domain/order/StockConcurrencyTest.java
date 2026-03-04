package com.loopers.domain.order;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StockConcurrencyTest {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductRepository productRepository;

    @DisplayName("동시에 10명이 같은 상품을 주문해도 재고가 정확히 차감된다")
    @Test
    void concurrentDecreaseStock() throws InterruptedException {
        // given
        ProductModel product = productService.register(1L, "테스트상품", "설명", 1000, 10, null);
        Long productId = product.getId();

        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger failCount = new AtomicInteger(0);

        // when -> 10명이 동시에 1개씩 차감
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {          // 일꾼에게 작업 배정 (비동기 실행)
                try {
                    productService.decreaseStockWithLock(productId, 1);  // 비관적 락으로 재고 차감
                } catch (Exception e) {
                    failCount.incrementAndGet();  // 실패하면 카운트 +1
                } finally {
                    latch.countDown();            // "나 끝났어!" → 카운트 -1
                }
            });
        }

        latch.await();

        // then -> 재고 0, 전원 성공
        ProductModel result = productRepository.findById(productId).orElseThrow();
        assertThat(result.getStockQuantity()).isEqualTo(0);
        assertThat(failCount.get()).isEqualTo(0);
    }
    
    @DisplayName("재고보다 많은 동시 주문이 들어오면, 초과분은 실패한다.")
    @Test
    void concurrentDecreaseStockExceed() throws InterruptedException {
        // given
        ProductModel product = productService.register(1L, "테스트상품2", "설명", 1000, 5, null);
        Long productId = product.getId();

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    productService.decreaseStockWithLock(productId, 1);
                    successCount.incrementAndGet();  // 성공하면 +1
                } catch (Exception e) {
                    failCount.incrementAndGet();     // 실패하면 +1 (재고 부족 예외)
                } finally {
                    latch.countDown();               // 카운트 -1
                }
            });
        }

        latch.await();

        ProductModel result = productRepository.findById(productId).orElseThrow();
        assertThat(result.getStockQuantity()).isEqualTo(0);
        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failCount.get()).isEqualTo(5);
    }
}
