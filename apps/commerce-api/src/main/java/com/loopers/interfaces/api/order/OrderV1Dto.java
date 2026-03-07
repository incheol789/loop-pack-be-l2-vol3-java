package com.loopers.interfaces.api.order;

import java.util.List;

public class OrderV1Dto {

    public record CreateRequest(
            List<OrderItemRequest> items,
            Long couponId
    ) {
    }

    public record OrderItemRequest(
            Long productId,
            int quantity
    ) {
    }

    public record OrderResponse(
            Long id,
            Long memberId,
            int originalAmount,
            int discountAmount,
            int totalAmount,
            Long userCouponId,
            List<OrderItemResponse> items
    ) {
    }

    public record OrderItemResponse(
            Long productId,
            String productName,
            String brandName,
            int productPrice,
            int quantity,
            int totalAmount
    ) {
    }

    public record OrderListResponse(
            List<OrderResponse> orders
    ) {
    }
}
