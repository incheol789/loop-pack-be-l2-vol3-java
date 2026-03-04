package com.loopers.application.product;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.product.ProductModel;

public record ProductDetailInfo(
        Long id,
        String name,
        String description,
        int price,
        int stockQuantity,
        String imageUrl,
        String brandName,
        long likeCount
) {
    public static ProductDetailInfo of(ProductModel product, BrandModel brand, long likeCount) {
        return new ProductDetailInfo(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                brand.getName(),
                likeCount
        );
    }
}
