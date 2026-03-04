package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    @Transactional
    public UserCouponModel issue(Long couponId, Long memberId) {
        return userCouponRepository.save(new UserCouponModel(couponId, memberId));
    }

    @Transactional(readOnly = true)
    public List<UserCouponModel> getByMemberId(Long memberId) {
        return userCouponRepository.findAllByMemberId(memberId);
    }

    @Transactional
    public UserCouponModel use(Long userCouponId, Long memberId) {
        UserCouponModel userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 쿠폰입니다."));

        userCoupon.validateOwner(memberId);
        userCoupon.use();

        return userCoupon;
    }

    @Transactional(readOnly = true)
    public Page<UserCouponModel> getByCouponId(Long couponId, Pageable pageable) {
        return userCouponRepository.findAllByCouponId(couponId, pageable);
    }
}
