package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductLikeModelTest {

    @DisplayName("좋아요를 생성할 때,")
    @Nested
    class Create {

        @DisplayName("유효한 정보면, 정상적으로 생성된다.")
        @Test
        void createLike() {
            // given
            Long memberId = 1L;
            Long productId = 1L;

            // when
            ProductLikeModel like = new ProductLikeModel(memberId, productId);

            // then
            assertAll(
                    () -> assertThat(like.getMemberId()).isEqualTo(memberId),
                    () -> assertThat(like.getProductId()).isEqualTo(productId)
            );
        }

        @DisplayName("회원 ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNullMemberId() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new ProductLikeModel(null, 1L)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("상품 ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNullProductId() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new ProductLikeModel(1L, null)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
