package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLikeModel;
import com.loopers.domain.like.ProductLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductLikeRepositoryImpl implements ProductLikeRepository {

    private final ProductLikeJpaRepository productLikeJpaRepository;

    @Override
    public ProductLikeModel save(ProductLikeModel like) {
        return productLikeJpaRepository.save(like);
    }

    @Override
    public void delete(ProductLikeModel like) {
        productLikeJpaRepository.delete(like);
    }

    @Override
    public Optional<ProductLikeModel> findByMemberIdAndProductId(Long memberId, Long productId) {
        return productLikeJpaRepository.findByMemberIdAndProductId(memberId, productId);
    }

    @Override
    public long countByProductId(Long productId) {
        return productLikeJpaRepository.countByProductId(productId);
    }
}
