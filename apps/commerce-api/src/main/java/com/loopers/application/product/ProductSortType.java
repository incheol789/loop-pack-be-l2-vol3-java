package com.loopers.application.product;

import lombok.Getter;

import java.util.Comparator;

@Getter
public enum ProductSortType {

    LATEST("최신순", Comparator.comparing(ProductDetailInfo::id).reversed()),
    PRICE_ASC("낮은 가격순", Comparator.comparing(ProductDetailInfo::price)),
    LIKES_DESC("좋아요 많은순", Comparator.comparing(ProductDetailInfo::likeCount).reversed());

    private final String description;
    private final Comparator<ProductDetailInfo> comparator;

    ProductSortType(String description, Comparator<ProductDetailInfo> comparator) {
        this.description = description;
        this.comparator = comparator;
    }

}
