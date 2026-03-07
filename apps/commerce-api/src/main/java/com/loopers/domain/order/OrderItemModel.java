package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "order_item")
@Getter
public class OrderItemModel extends BaseEntity {

    private Long orderId;
    private Long productId;
    private String productName;
    private String brandName;
    private int productPrice;
    private int quantity;

    protected OrderItemModel() {
    }

    public OrderItemModel(Long productId, String productName, String brandName, int productPrice, int quantity) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다.");
        }
        if (productName == null || productName.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품명은 비어있을 수 없습니다.");
        }
        if (brandName == null || brandName.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드명은 비어있을 수 없습니다.");
        }
        if (productPrice < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 가격은 0 이상이어야 합니다.");
        }
        if (quantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 수량은 1 이상이어야 합니다.");
        }
        this.productId = productId;
        this.productName = productName;
        this.brandName = brandName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public void assignOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getTotalAmount() {
        return productPrice * quantity;
    }
}
