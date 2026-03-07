package com.loopers.interfaces.api.coupon;

import com.loopers.domain.coupon.CouponType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CouponV1Dto {

    public record UserCouponResponse(
            Long userCouponId,
            String couponName,
            CouponType type,
            BigDecimal value,
            BigDecimal minOrderAmount,
            String status,
            ZonedDateTime expiredAt
    ) {}
}
