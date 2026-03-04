package com.loopers.domain.point;

import com.loopers.domain.vo.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class PointModelTest {

    @DisplayName("포인트를 생성할 때,")
    @Nested
    class Create {

        @DisplayName("유효한 회원 ID면, 잔액이 0으로 생성된다.")
        @Test
        void createPoint() {
            // given
            Long memberId = 1L;

            // when
            PointModel point = new PointModel(memberId);

            // then
            assertAll(
                    () -> assertThat(point.getMemberId()).isEqualTo(memberId),
                    () -> assertThat(point.getBalanceMoney()).isEqualTo(Money.of(0))
            );
        }

        @DisplayName("회원 ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNullMemberId() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new PointModel(null)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트를 충전할 때,")
    @Nested
    class Charge {

        @DisplayName("양수 금액이면, 정상적으로 충전된다.")
        @Test
        void chargeSuccess() {
            // given
            PointModel point = new PointModel(1L);

            // when
            point.charge(Money.of(10000));

            // then
            assertThat(point.getBalanceMoney()).isEqualTo(Money.of(10000));
        }

        @DisplayName("0 이하 금액이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithZeroAmount() {
            // given
            PointModel point = new PointModel(1L);

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    point.charge(Money.zero())
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트를 사용할 때,")
    @Nested
    class Use {

        @DisplayName("충분한 잔액이 있으면, 정상적으로 차감된다.")
        @Test
        void useSuccess() {
            // given
            PointModel point = new PointModel(1L);
            point.charge(Money.of(10000));

            // when
            point.use(Money.of(3000));

            // then
            assertThat(point.getBalanceMoney()).isEqualTo(Money.of(7000));
        }

        @DisplayName("잔액이 부족하면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithInsufficientBalance() {
            // given
            PointModel point = new PointModel(1L);
            point.charge(Money.of(1000));

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    point.use(Money.of(5000))
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("사용 금액이 0 이하이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithZeroAmount() {
            // given
            PointModel point = new PointModel(1L);

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    point.use(Money.zero())
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
        
        @DisplayName("잔액과 동일한 금액을 사용하면, 잔액이 0이 된다.")
        @Test
        void useExactBalance() {
            // given
            PointModel point = new PointModel(1L);
            point.charge(Money.of(5000));

            // when
            point.use(Money.of(5000));
            
            // then
            assertThat(point.getBalanceMoney()).isEqualTo(Money.of(0));
        }
    }
}
