package com.loopers.domain.like;

import java.util.Optional;

public interface ProductLikeRepository {
    ProductLikeModel save(ProductLikeModel like);
    void delete(ProductLikeModel like);
    Optional<ProductLikeModel> findByMemberIdAndProductId(Long memberId, Long productId);
    long countByProductId(Long productId);
}
