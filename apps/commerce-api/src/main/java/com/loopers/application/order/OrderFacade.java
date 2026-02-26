package com.loopers.application.order;

import com.loopers.domain.brand.BrandService;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.vo.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderFacade {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ProductService productService;
    private final BrandService brandService;
    private final PointService pointService;

    @Transactional
    public OrderInfo createOrder(String loginId, String password, List<OrderItemRequest> itemRequests) {
        // 1. 회원 인증
        MemberModel member = memberService.getMyInfo(loginId, password);

        // 2. 주문 항목 생성 (상품 스냅샷 + 재고 차감)
        List<OrderItemModel> orderItems = new ArrayList<>();
        for (OrderItemRequest req : itemRequests) {
            ProductModel product = productService.getById(req.productId());
            productService.decreaseStock(product.getId(), req.quantity());

            OrderItemModel item = new OrderItemModel(
                    product.getId(),
                    product.getName(),
                    brandService.getById(product.getBrandId()).getName(),
                    product.getPrice(),
                    req.quantity()
            );
            orderItems.add(item);
        }

        // 3. 주문 생성
        OrderModel order = orderService.createOrder(member.getId(), orderItems);

        // 4. 포인트 차감
        pointService.use(member.getId(), Money.of(order.getTotalAmount()));

        List<OrderItemModel> savedItems = orderService.getOrderItems(order.getId());
        return OrderInfo.from(order, savedItems);
    }

    @Transactional(readOnly = true)
    public OrderInfo getById(Long id) {
        OrderModel order = orderService.getById(id);
        List<OrderItemModel> items = orderService.getOrderItems(order.getId());
        return OrderInfo.from(order, items);
    }

    @Transactional(readOnly = true)
    public List<OrderInfo> getByMember(String loginId, String password) {
        MemberModel member = memberService.getMyInfo(loginId, password);
        List<OrderModel> orders = orderService.getByMemberId(member.getId());

        return orders.stream()
                .map(order -> {
                    List<OrderItemModel> items = orderService.getOrderItems(order.getId());
                    return OrderInfo.from(order, items);
                })
                .toList();
    }

    public record OrderItemRequest(
            Long productId,
            int quantity
    ) {
    }
}
