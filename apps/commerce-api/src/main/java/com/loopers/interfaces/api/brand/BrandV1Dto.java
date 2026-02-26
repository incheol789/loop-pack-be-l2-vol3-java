package com.loopers.interfaces.api.brand;

public class BrandV1Dto {

    // 브랜드 등록 요청
    public record RegisterRequest(
            String name,
            String description,
            String imageUrl
    ) {
    }

    // 브랜드 응답
    public record BrandResponse(
            Long id,
            String name,
            String description,
            String imageUrl
    ) {
    }
}
