package com.loopers.domain.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Percentage(int value) {

    public Percentage {
        if (value <= 0 || value > 100) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인율은 1 이상 100 이하여야 합니다.");
        }
    }

    public Money calculateDiscount(Money price) {
        BigDecimal rate = BigDecimal.valueOf(value)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal discounted = price.amount()
                .multiply(rate)
                .setScale(0, RoundingMode.DOWN);  // 원 단위 절삭
        return new Money(discounted);
    }

    public static Percentage of(int value) {
        return new Percentage(value);
    }
}
