package com.loopers.domain.vo;

import java.math.BigDecimal;

public record Money(BigDecimal amount) {

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("금액은 null일 수 없습니다.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("금액은 0 이상이어야 합니다.");
        }
    }

    public Money plus(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money minus(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }

    public static Money of(long value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

}
