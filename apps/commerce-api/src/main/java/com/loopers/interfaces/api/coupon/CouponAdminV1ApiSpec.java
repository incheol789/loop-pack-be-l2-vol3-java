package com.loopers.interfaces.api.coupon;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Coupon Admin V1 API", description = "쿠폰 관리자 API")
public interface CouponAdminV1ApiSpec {

    @Operation(summary = "쿠폰 템플릿 등록")
    ApiResponse<CouponAdminV1Dto.CouponResponse> register(CouponAdminV1Dto.RegisterRequest request);

    @Operation(summary = "쿠폰 템플릿 목록 조회")
    ApiResponse<Page<CouponAdminV1Dto.CouponResponse>> getAll(Pageable pageable);

    @Operation(summary = "쿠폰 템플릿 상세 조회")
    ApiResponse<CouponAdminV1Dto.CouponResponse> getById(Long couponId);

    @Operation(summary = "쿠폰 템플릿 수정")
    ApiResponse<CouponAdminV1Dto.CouponResponse> modifyInfo(Long couponId, CouponAdminV1Dto.ModifyRequest request);

    @Operation(summary = "쿠폰 템플릿 삭제")
    ApiResponse<Void> delete(Long couponId);

    @Operation(summary = "쿠폰 발급 내역 조회")
    ApiResponse<Page<CouponAdminV1Dto.UserCouponResponse>> getIssues(Long couponId, Pageable pageable);
}
