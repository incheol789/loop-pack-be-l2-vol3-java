package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderModel createOrder(Long memberId, List<OrderItemModel> orderItems,
                                  Long userCouponId, int discountAmount) {
        OrderModel order = new OrderModel(memberId, orderItems, userCouponId, discountAmount);
        OrderModel saveOrder = orderRepository.save(order);

        for (OrderItemModel item : orderItems) {
            item.assignOrderId(saveOrder.getId());
            orderRepository.saveItem(item);
        }

        return saveOrder;
    }

    @Transactional(readOnly = true)
    public OrderModel getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문입니다."));
    }

    @Transactional(readOnly = true)
    public List<OrderModel> getByMemberId(Long memberId) {
        return orderRepository.findAllByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<OrderItemModel> getOrderItems(Long orderId) {
        return orderRepository.findItemsByOrderId(orderId);
    }
}
