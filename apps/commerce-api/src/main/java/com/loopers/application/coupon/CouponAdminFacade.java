package com.loopers.application.coupon;

import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.coupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponAdminFacade {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Transactional
    public CouponInfo register(CouponService.RegisterCommand command) {
        CouponModel coupon = couponService.register(command);
        return CouponInfo.from(coupon);
    }

    @Transactional
    public CouponInfo modifyInfo(Long id, CouponService.ModifyCommand command) {
        CouponModel coupon = couponService.modifyInfo(id, command);
        return CouponInfo.from(coupon);
    }

    @Transactional(readOnly = true)
    public CouponInfo getById(Long id) {
        return CouponInfo.from(couponService.getById(id));
    }

    @Transactional(readOnly = true)
    public Page<CouponInfo> getAll(Pageable pageable) {
        return couponService.getAll(pageable)
                .map(CouponInfo::from);
    }

    @Transactional
    public void delete(Long id) {
        couponService.delete(id);
    }

    @Transactional(readOnly = true)
    public Page<UserCouponInfo> getIssuesByCouponId(Long couponId, Pageable pageable) {
        CouponModel coupon = couponService.getById(couponId);
        return userCouponService.getByCouponId(couponId, pageable)
                .map(uc -> UserCouponInfo.from(uc, coupon));
    }
}
