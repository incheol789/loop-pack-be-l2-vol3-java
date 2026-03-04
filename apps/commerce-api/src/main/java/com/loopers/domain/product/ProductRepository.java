package com.loopers.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    ProductModel save(ProductModel product);
    Optional<ProductModel> findById(Long id);
    Optional<ProductModel> findByIdWithLock(Long id);  // 비관적락 추가
    List<ProductModel> findAllByBrandId(Long brandId);
    List<ProductModel> findAll();
}
