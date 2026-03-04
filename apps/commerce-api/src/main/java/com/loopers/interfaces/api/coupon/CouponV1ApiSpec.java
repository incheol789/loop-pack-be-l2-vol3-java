package com.loopers.interfaces.api.coupon;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Coupon V1 API", description = "쿠폰 사용자 API")
public interface CouponV1ApiSpec {

    @Operation(summary = "쿠폰 발급")
    ApiResponse<CouponV1Dto.UserCouponResponse> issue(Long couponId, Long memberId);

    @Operation(summary = "내 쿠폰 목록 조회")
    ApiResponse<List<CouponV1Dto.UserCouponResponse>> getMyCoupons(Long memberId);
}
