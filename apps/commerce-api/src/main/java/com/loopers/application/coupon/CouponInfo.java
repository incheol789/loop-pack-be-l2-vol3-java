package com.loopers.application.coupon;

import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record CouponInfo(
        Long id,
        String name,
        CouponType type,
        BigDecimal discountAmount,
        Integer discountRate,
        BigDecimal minOrderAmount,
        ZonedDateTime expiredAt
) {
    public static CouponInfo from(CouponModel model) {
        return new CouponInfo(
                model.getId(),
                model.getName(),
                model.getType(),
                model.getDiscountAmount() != null ? model.getDiscountAmount().amount() : null,
                model.getDiscountRate(),
                model.getMinOrderAmount() != null ? model.getMinOrderAmount().amount() : null,
                model.getExpiredAt()
        );
    }
}
