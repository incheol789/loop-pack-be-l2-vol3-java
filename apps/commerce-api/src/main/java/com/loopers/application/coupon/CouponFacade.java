package com.loopers.application.coupon;

import com.loopers.domain.coupon.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Transactional
    public UserCouponInfo issue(Long couponId, Long memberId) {
        CouponModel coupon = couponService.getById(couponId);
        coupon.validateNotExpired();
        UserCouponModel userCoupon = userCouponService.issue(couponId, memberId);
        return UserCouponInfo.from(userCoupon, coupon);
    }

    @Transactional(readOnly = true)
    public List<UserCouponInfo> getMyCoupons(Long memberId) {
        List<UserCouponModel> userCoupons = userCouponService.getByMemberId(memberId);
        if (userCoupons.isEmpty()) {
            return List.of();
        }

        List<Long> couponIds = userCoupons.stream()
                .map(UserCouponModel::getCouponId)
                .distinct()
                .toList();

        Map<Long, CouponModel> couponMap = couponService.getAllByIds(couponIds).stream()
                .collect(Collectors.toMap(CouponModel::getId, Function.identity()));

        return userCoupons.stream()
                .filter(uc -> couponMap.containsKey(uc.getCouponId()))
                .map(uc -> UserCouponInfo.from(uc, couponMap.get(uc.getCouponId())))
                .toList();
    }
}
