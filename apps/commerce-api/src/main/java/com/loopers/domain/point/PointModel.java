package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.vo.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "point")
@Getter
public class PointModel extends BaseEntity {

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private BigDecimal balance;

    protected PointModel() {}

    public PointModel(Long memberId) {
        if (memberId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "회원 ID는 필수입니다.");
        }
        this.memberId = memberId;
        this.balance = BigDecimal.ZERO;
    }

    public Money getBalanceMoney() {
        return new Money(this.balance);
    }

    public void charge(Money amount) {
        if (!amount.isGreaterThanOrEqual(Money.of(1))) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 1원 이상이어야 합니다.");
        }
        this.balance = this.balance.add(amount.amount());
    }

    public void use(Money amount) {
        if (!amount.isGreaterThanOrEqual(Money.of(1))) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용 금액은 1원 이상이어야 합니다.");
        }
        if (!getBalanceMoney().isGreaterThanOrEqual(amount)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트가 부족합니다.");
        }
        this.balance = this.balance.subtract(amount.amount());
    }
}
