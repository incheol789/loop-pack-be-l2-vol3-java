package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LikeV1Controller implements LikeV1ApiSpec {

    private final LikeFacade likeFacade;

    @PostMapping("/api/v1/products/{productId}/likes")
    @Override
    public ApiResponse<Void> addLike(
            @RequestHeader("X-Loopers-LoginId") String loginId,
            @RequestHeader("X-Loopers-LoginPw") String loginPw,
            @PathVariable Long productId
    ) {
        likeFacade.addLike(loginId, loginPw, productId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/api/v1/products/{productId}/likes")
    @Override
    public ApiResponse<Void> removeLike(
            @RequestHeader("X-Loopers-LoginId") String loginId,
            @RequestHeader("X-Loopers-LoginPw") String loginPw,
            @PathVariable Long productId
    ) {
        likeFacade.removeLike(loginId, loginPw, productId);
        return ApiResponse.success(null);
    }

    @GetMapping("/api/v1/likes/count")
    @Override
    public ApiResponse<LikeV1Dto.CountResponse> count(
            @RequestParam Long productId
    ) {
        long count = likeFacade.countByProductId(productId);
        return ApiResponse.success(new LikeV1Dto.CountResponse(productId, count));
    }
}
