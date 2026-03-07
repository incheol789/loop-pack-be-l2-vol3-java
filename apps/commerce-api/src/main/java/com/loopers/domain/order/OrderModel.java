package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.List;

@Entity
@Table(name = "orders")
@Getter
public class OrderModel extends BaseEntity {

    private Long memberId;
    private int originalAmount;
    private int discountAmount;
    private int totalAmount;
    private Long userCouponId;
    private String status;

    protected OrderModel() {
    }

    public OrderModel(Long memberId, List<OrderItemModel> orderItems,
                      Long userCouponId, int discountAmount) {
        if (memberId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "회원 ID는 필수입니다.");
        }
        if (orderItems == null || orderItems.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 항목은 1개 이상이어야 합니다.");
        }
        if (discountAmount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 금액은 0 이상이어야 합니다.");
        }
        this.memberId = memberId;
        this.originalAmount = orderItems.stream()
                .mapToInt(OrderItemModel::getTotalAmount)
                .sum();
        this.discountAmount = discountAmount;
        this.totalAmount = this.originalAmount - this.discountAmount;
        this.userCouponId = userCouponId;
        this.status = "CREATED";
    }
}
