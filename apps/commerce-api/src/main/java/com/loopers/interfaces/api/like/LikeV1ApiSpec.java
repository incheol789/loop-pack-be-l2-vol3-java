package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Like V1 API", description = "좋아요 API 입니다.")
public interface LikeV1ApiSpec {

    @Operation(summary = "좋아요 등록", description = "상품에 좋아요를 등록합니다.")
    ApiResponse<Void> addLike(
            @Parameter(description = "로그인 ID") String loginId,
            @Parameter(description = "로그인 비밀번호") String loginPw,
            @Parameter(description = "상품 ID") Long productId
    );

    @Operation(summary = "좋아요 취소", description = "상품의 좋아요를 취소합니다.")
    ApiResponse<Void> removeLike(
            @Parameter(description = "로그인 ID") String loginId,
            @Parameter(description = "로그인 비밀번호") String loginPw,
            @Parameter(description = "상품 ID") Long productId
    );

    @Operation(summary = "좋아요 수 조회", description = "상품의 좋아요 수를 조회합니다.")
    ApiResponse<LikeV1Dto.CountResponse> count(@Parameter(description = "상품 ID") Long productId);
}
