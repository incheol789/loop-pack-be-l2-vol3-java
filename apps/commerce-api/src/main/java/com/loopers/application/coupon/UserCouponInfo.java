package com.loopers.application.coupon;

import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.UserCouponModel;
import com.loopers.domain.coupon.UserCouponStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record UserCouponInfo(
        Long userCouponId,
        String couponName,
        CouponType type,
        BigDecimal discountAmount,
        Integer discountRate,
        BigDecimal minOrderAmount,
        UserCouponStatus status,
        ZonedDateTime expiredAt
) {
    public static UserCouponInfo from(UserCouponModel userCoupon, CouponModel coupon) {
        return new UserCouponInfo(
                userCoupon.getId(),
                coupon.getName(),
                coupon.getType(),
                coupon.getDiscountAmount() != null ? coupon.getDiscountAmount().amount() : null,
                coupon.getDiscountRate(),
                coupon.getMinOrderAmount() != null ? coupon.getMinOrderAmount().amount() : null,
                userCoupon.getStatus(),
                coupon.getExpiredAt()
        );
    }
}
