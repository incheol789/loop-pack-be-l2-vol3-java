package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserCouponModelTest {

    @DisplayName("발급된 쿠폰을 생성할 때,")
    @Nested
    class Create {

        @DisplayName("정상적으로 생성된다 (status = AVAILABLE)")
        @Test
        void createSuccess() {
            // given & when
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);

            // then
            assertThat(userCoupon.getCouponId()).isEqualTo(1L);
            assertThat(userCoupon.getMemberId()).isEqualTo(100L);
            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
        }

        @DisplayName("couponId가 null이면 예외가 발생한다.")
        @Test
        void failWithoutCouponId() {
            assertThatThrownBy(() -> new UserCouponModel(null, 100L))
                    .isInstanceOf(CoreException.class);
        }

        @DisplayName("memberId가 null이면 예외가 발생한다.")
        @Test
        void failWithoutMemberId() {
            assertThatThrownBy(() -> new UserCouponModel(1L, null))
                    .isInstanceOf(CoreException.class);
        }
    }

    @DisplayName("쿠폰을 사용할 때,")
    @Nested
    class Use {

        @DisplayName("AVAILABLE 상태이면 USED로 변경된다.")
        @Test
        void useSuccess() {
            // given
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);

            // when
            userCoupon.use();

            // then
            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
        }

        @DisplayName("usedAt이 설정된다.")
        @Test
        void usedAtIsSet() {
            // given
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);

            // when
            userCoupon.use();

            // then
            assertThat(userCoupon.getUsedAt()).isNotNull();
        }

        @DisplayName("이미 USED 상태이면 예외가 발생한다.")
        @Test
        void failWhenAlreadyUsed() {
            // given
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);
            userCoupon.use();

            // when & then
            assertThatThrownBy(userCoupon::use)
                    .isInstanceOf(CoreException.class);
        }
    }

    @DisplayName("소유자를 검증할 때,")
    @Nested
    class ValidateOwner {

        @DisplayName("본인의 쿠폰이면 통과한다.")
        @Test
        void passWhenOwner() {
            // given
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);

            // when & then (예외 없음)
            userCoupon.validateOwner(100L);
        }

        @DisplayName("타인의 쿠폰이면 예외가 발생한다.")
        @Test
        void failWhenNotOwner() {
            // given
            UserCouponModel userCoupon = new UserCouponModel(1L, 100L);

            // when & then
            assertThatThrownBy(() -> userCoupon.validateOwner(999L))
                    .isInstanceOf(CoreException.class);
        }
    }
}
