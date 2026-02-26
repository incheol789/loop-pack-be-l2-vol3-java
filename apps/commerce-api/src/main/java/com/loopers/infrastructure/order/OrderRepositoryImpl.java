package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderModel save(OrderModel order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<OrderModel> findById(Long id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public List<OrderModel> findAllByMemberId(Long memberId) {
        return orderJpaRepository.findAllByMemberId(memberId);
    }

    @Override
    public OrderItemModel saveItem(OrderItemModel orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }

    @Override
    public List<OrderItemModel> findItemsByOrderId(Long orderId) {
        return orderItemJpaRepository.findAllByOrderId(orderId);
    }
}
