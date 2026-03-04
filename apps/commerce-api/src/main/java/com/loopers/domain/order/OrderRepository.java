package com.loopers.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    OrderModel save(OrderModel order);
    Optional<OrderModel> findById(Long id);
    List<OrderModel> findAllByMemberId(Long memberId);
    OrderItemModel saveItem(OrderItemModel orderItem);
    List<OrderItemModel> findItemsByOrderId(Long orderId);
}
