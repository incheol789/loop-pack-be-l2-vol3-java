package com.loopers.interfaces.api.order;

import java.util.List;

public class OrderV1Dto {

    public record CreateRequest(
            List<OrderItemRequest> items
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
            int totalAmount,
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
