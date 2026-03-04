package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductLikeServiceUnitTest {

    @Mock
    private ProductLikeRepository productLikeRepository;

    @InjectMocks
    private ProductLikeService productLikeService;

    @DisplayName("좋아요를 등록할 때,")
    @Nested
    class AddLike {

        @DisplayName("기존 좋아요가 없으면, 새로 생성된다.")
        @Test
        void createNewLike() {
            // given
            Long memberId = 1L;
            Long productId = 1L;
            when(productLikeRepository.findByMemberIdAndProductId(memberId, productId))
                    .thenReturn(Optional.empty());
            when(productLikeRepository.save(any(ProductLikeModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            productLikeService.addLike(memberId, productId);

            // then
            verify(productLikeRepository, times(1)).save(any(ProductLikeModel.class));
        }

        @DisplayName("기존 좋아요가 있으면, CONFLICT 예외가 발생한다.")
        @Test
        void failWithExistingLike() {
            // given
            Long memberId = 1L;
            Long productId = 1L;
            ProductLikeModel like = new ProductLikeModel(memberId, productId);
            when(productLikeRepository.findByMemberIdAndProductId(memberId, productId))
                    .thenReturn(Optional.of(like));

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    productLikeService.addLike(memberId, productId)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayName("좋아요를 취소할 때,")
    @Nested
    class RemoveLike {

        @DisplayName("기존 좋아요가 있으면, 삭제된다.")
        @Test
        void deleteLike() {
            // given
            Long memberId = 1L;
            Long productId = 1L;
            ProductLikeModel like = new ProductLikeModel(memberId, productId);
            when(productLikeRepository.findByMemberIdAndProductId(memberId, productId))
                    .thenReturn(Optional.of(like));

            // when
            productLikeService.removeLike(memberId, productId);

            // then
            verify(productLikeRepository, times(1)).delete(like);
        }

        @DisplayName("기존 좋아요가 없으면, NOT_FOUND 예외가 발생한다.")
        @Test
        void failWithNoLike() {
            // given
            Long memberId = 1L;
            Long productId = 1L;
            when(productLikeRepository.findByMemberIdAndProductId(memberId, productId))
                    .thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    productLikeService.removeLike(memberId, productId)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("상품의 좋아요 수를 조회할 때,")
    @Nested
    class CountByProductId {

        @DisplayName("좋아요 수가 반환된다.")
        @Test
        void countSuccess() {
            // given
            when(productLikeRepository.countByProductId(1L)).thenReturn(5L);

            // when
            long count = productLikeService.countByProductId(1L);

            // then
            assertThat(count).isEqualTo(5L);
        }
    }
}
