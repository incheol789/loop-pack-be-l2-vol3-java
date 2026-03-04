package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceUnitTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    @DisplayName("쿠폰을 발급할 때,")
    @Nested
    class Issue {

        @DisplayName("정상적으로 발급된다.")
        @Test
        void issueSuccess() {
            // given
            when(userCouponRepository.save(any(UserCouponModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            UserCouponModel result = userCouponService.issue(1L, 100L);

            // then
            assertAll(
                    () -> assertThat(result.getCouponId()).isEqualTo(1L),
                    () -> assertThat(result.getMemberId()).isEqualTo(100L),
                    () -> assertThat(result.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE)
            );
            verify(userCouponRepository, times(1)).save(any(UserCouponModel.class));
        }
    }

    @DisplayName("쿠폰을 사용할 때,")
    @Nested
    class Use {

        @DisplayName("본인 소유의 AVAILABLE 쿠폰이면, USED로 변경된다.")
        @Test
        void useSuccess() {
            // given
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);
            when(userCouponRepository.findById(1L)).thenReturn(Optional.of(userCoupon));

            // when
            UserCouponModel result = userCouponService.use(1L, 100L);

            // then
            assertThat(result.getStatus()).isEqualTo(UserCouponStatus.USED);
        }

        @DisplayName("존재하지 않는 쿠폰이면, NOT_FOUND 예외가 발생한다.")
        @Test
        void failWithNotFound() {
            // given
            when(userCouponRepository.findById(999L)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class,
                    () -> userCouponService.use(999L, 100L));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("본인 소유가 아니면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNotOwner() {
            // given
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);
            when(userCouponRepository.findById(1L)).thenReturn(Optional.of(userCoupon));

            // when
            CoreException result = assertThrows(CoreException.class,
                    () -> userCouponService.use(1L, 999L));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이미 사용된 쿠폰이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithAlreadyUsed() {
            // given
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);
            userCoupon.use(); // 먼저 사용 처리
            when(userCouponRepository.findById(1L)).thenReturn(Optional.of(userCoupon));

            // when
            CoreException result = assertThrows(CoreException.class,
                    () -> userCouponService.use(1L, 100L));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("회원별 쿠폰 목록을 조회할 때,")
    @Nested
    class GetByMemberId {

        @DisplayName("해당 회원의 쿠폰 목록이 반환된다.")
        @Test
        void getByMemberIdSuccess() {
            // given
            List<UserCouponModel> coupons = List.of(
                    new UserCouponModel(1L, 100L),
                    new UserCouponModel(2L, 100L)
            );
            when(userCouponRepository.findAllByMemberId(100L)).thenReturn(coupons);

            // when
            List<UserCouponModel> result = userCouponService.getByMemberId(100L);

            // then
            assertThat(result).hasSize(2);
        }
    }

    @DisplayName("쿠폰별 발급 내역을 조회할 때,")
    @Nested
    class GetByCouponId {

        @DisplayName("해당 쿠폰의 발급 내역이 페이징되어 반환된다.")
        @Test
        void getByCouponIdSuccess() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            List<UserCouponModel> coupons = List.of(
                    new UserCouponModel(1L, 100L),
                    new UserCouponModel(1L, 200L)
            );
            when(userCouponRepository.findAllByCouponId(1L, pageable))
                    .thenReturn(new PageImpl<>(coupons, pageable, coupons.size()));

            // when
            Page<UserCouponModel> result = userCouponService.getByCouponId(1L, pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
        }
    }
}

