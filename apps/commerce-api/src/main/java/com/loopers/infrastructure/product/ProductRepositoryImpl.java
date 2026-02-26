package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public ProductModel save(ProductModel product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<ProductModel> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<ProductModel> findAllByBrandId(Long brandId) {
        return productJpaRepository.findAllByBrandId(brandId);
    }

    @Override
    public List<ProductModel> findAll() {
        return productJpaRepository.findAll();
    }
}
