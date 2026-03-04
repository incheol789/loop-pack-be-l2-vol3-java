package com.loopers.domain.like;

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
class LikeConcurrencyTest {

    @Autowired
    private ProductLikeService productLikeService;

    @Autowired
    private ProductLikeRepository productLikeRepository;

    @DisplayName("동일 상품에 여러 명이 동시에 좋아요해도, 좋아요 수가 정상 반영된다")
    @Test
    void concurrentAddLike() throws InterruptedException {
        // given
        Long productId = 999L;
        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when: 서로 다른 회원 10명이 동시에 같은 상품에 좋아요
        for (int i = 0; i < threadCount; i++) {
            long memberId = i + 1;
            executor.submit(() -> {
                try {
                    productLikeService.addLike(memberId, productId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then: 좋아요 수 == 성공 수, 전원 성공
        long likeCount = productLikeRepository.countByProductId(productId);
        assertThat(likeCount).isEqualTo(successCount.get());
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isEqualTo(0);
    }

    @DisplayName("같은 회원이 동시에 좋아요를 두 번 눌러도, 1번만 저장된다")
    @Test
    void concurrentDuplicateLike() throws InterruptedException {
        // given
        Long memberId = 100L;
        Long productId = 998L;
        int threadCount = 2;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when: 같은 회원이 동시에 2번 좋아요 시도
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    productLikeService.addLike(memberId, productId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then: UniqueConstraint 덕분에 1번만 저장, 1번은 실패
        long likeCount = productLikeRepository.countByProductId(productId);
        assertThat(likeCount).isEqualTo(1);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}
