package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product V1 API", description = "상품 API 입니다.")
public interface ProductV1ApiSpec {

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    ApiResponse<ProductV1Dto.ProductResponse> register(ProductV1Dto.RegisterRequest request);

    @Operation(summary = "상품 상세 조회", description = "ID로 상품을 조회합니다. 브랜드명과 좋아요 수를 포함합니다.")
    ApiResponse<ProductV1Dto.ProductDetailResponse> getById(@Parameter(description = "상품 ID") Long id);

    @Operation(summary = "상품 목록 조회", description = "정렬 조건에 따라 상품 목록을 조회합니다.")
    ApiResponse<ProductV1Dto.ProductListResponse> getProducts(
            @Parameter(description = "정렬: LATEST, PRICE_ASC, LIKES_DESC") String sortType,
            @Parameter(description = "브랜드 ID (선택)") Long brandId
    );
}
