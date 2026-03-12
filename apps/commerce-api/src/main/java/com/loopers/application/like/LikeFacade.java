package com.loopers.application.like;

import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class LikeFacade {

    private final ProductLikeService productLikeService;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public void addLike(String loginId, String password, Long productId) {
        MemberModel member = memberService.getMyInfo(loginId, password);
        productService.getById(productId);
        productLikeService.addLike(member.getId(), productId);
        productService.increaseLikeCount(productId);
    }

    @Transactional
    public void removeLike(String loginId, String password, Long productId) {
        MemberModel member = memberService.getMyInfo(loginId, password);
        productService.getById(productId);
        productLikeService.removeLike(member.getId(), productId);
        productService.decreaseLikeCount(productId);
    }

    @Transactional(readOnly = true)
    public long countByProductId(Long productId) {
        return productLikeService.countByProductId(productId);
    }
}
