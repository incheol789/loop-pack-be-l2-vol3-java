package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;

    @Transactional
    public void addLike(Long memberId, Long productId) {
        productLikeRepository.findByMemberIdAndProductId(memberId, productId)
                .ifPresent(like -> {
                    throw new CoreException(ErrorType.CONFLICT, "이미 좋아요한 상품입니다.");
                });

        productLikeRepository.save(new ProductLikeModel(memberId, productId));
    }

    @Transactional
    public void removeLike(Long memberId, Long productId) {
        ProductLikeModel like = productLikeRepository.findByMemberIdAndProductId(memberId, productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "좋아요하지 않은 상품입니다."));

        productLikeRepository.delete(like);
    }

    @Transactional(readOnly = true)
    public long countByProductId(Long productId) {
        return productLikeRepository.countByProductId(productId);
    }
}
