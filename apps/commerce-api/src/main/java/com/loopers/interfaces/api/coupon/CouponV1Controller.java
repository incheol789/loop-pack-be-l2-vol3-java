package com.loopers.interfaces.api.coupon;

import com.loopers.application.coupon.CouponFacade;
import com.loopers.application.coupon.UserCouponInfo;
import com.loopers.domain.coupon.CouponType;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponV1Controller implements CouponV1ApiSpec {

    private final CouponFacade couponFacade;

    @PostMapping("/api/v1/coupons/{couponId}/issue")
    @Override
    public ApiResponse<CouponV1Dto.UserCouponResponse> issue(
            @PathVariable Long couponId,
            @RequestHeader("X-USER-ID") Long memberId
    ) {
        UserCouponInfo info = couponFacade.issue(couponId, memberId);
        return ApiResponse.success(toUserCouponResponse(info));
    }

    @GetMapping("/api/v1/users/me/coupons")
    @Override
    public ApiResponse<List<CouponV1Dto.UserCouponResponse>> getMyCoupons(
            @RequestHeader("X-USER-ID") Long memberId
    ) {
        List<CouponV1Dto.UserCouponResponse> responses = couponFacade.getMyCoupons(memberId).stream()
                .map(this::toUserCouponResponse)
                .toList();
        return ApiResponse.success(responses);
    }

    private CouponV1Dto.UserCouponResponse toUserCouponResponse(UserCouponInfo info) {
        return new CouponV1Dto.UserCouponResponse(
                info.userCouponId(), info.couponName(), info.type(),
                resolveValue(info.type(), info.discountAmount(), info.discountRate()),
                info.minOrderAmount(), info.status().name(), info.expiredAt()
        );
    }

    private BigDecimal resolveValue(CouponType type, BigDecimal discountAmount, Integer discountRate) {
        return type == CouponType.FIXED ? discountAmount : BigDecimal.valueOf(discountRate);
    }
}
