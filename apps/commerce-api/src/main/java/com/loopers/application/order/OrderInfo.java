package com.loopers.application.order;

import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderModel;

import java.util.List;

public record OrderInfo(
        Long id,
        Long memberId,
        int totalAmount,
        List<OrderItemInfo> items
) {
    public record OrderItemInfo(
            Long productId,
            String productName,
            String brandName,
            int productPrice,
            int quantity,
            int totalAmount
    ) {
        public static OrderItemInfo from(OrderItemModel model) {
            return new OrderItemInfo(
                    model.getProductId(),
                    model.getProductName(),
                    model.getBrandName(),
                    model.getProductPrice(),
                    model.getQuantity(),
                    model.getTotalAmount()
            );
        }
    }

    public static OrderInfo from(OrderModel order, List<OrderItemModel> items) {
        return new OrderInfo(
                order.getId(),
                order.getMemberId(),
                order.getTotalAmount(),
                items.stream().map(OrderItemInfo::from).toList()
        );
    }
}
