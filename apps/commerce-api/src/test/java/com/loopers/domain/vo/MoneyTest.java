package com.loopers.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyTest {

    @DisplayName("Money를 생성할 때,")
    @Nested
    class Create {

        @DisplayName("양수 금액이면, 정상적으로 생성된다.")
        @Test
        void createSuccess() {
            // given & when
            Money money = Money.of(1000);

            // then
            assertThat(money.amount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        }

        @DisplayName("0이면, 정상적으로 생성된다.")
        @Test
        void createZero() {
            // given & when
            Money money = Money.zero();

            // then
            assertThat(money.amount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @DisplayName("음수 금액이면, 예외가 발생한다.")
        @Test
        void failWithNegativeAmount() {
            // given & when & then
            assertThrows(IllegalArgumentException.class, () ->
                    new Money(BigDecimal.valueOf(-1)));
        }

        @DisplayName("null이면, 예외가 발생한다.")
        @Test
        void failWithNull() {
            // given & when & then
            assertThrows(IllegalArgumentException.class, () ->
                    new Money(null));
        }
    }

    @DisplayName("Money 연산을 수행할 때,")
    @Nested
    class Operations {

        @DisplayName("plus: 두 금액을 더한다.")
        @Test
        void plus() {
            // given
            Money a = Money.of(1000);
            Money b = Money.of(2000);

            // when
            Money result = a.plus(b);

            // then
            assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        }

        @DisplayName("minus: 두 금액을 뺀다.")
        @Test
        void minus() {
            // given
            Money a = Money.of(3000);
            Money b = Money.of(1000);

            // when
            Money result = a.minus(b);

            // then
            assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(2000));
        }

        @DisplayName("minus: 결과가 음수면 예외가 발생한다.")
        @Test
        void minusFailWithNegativeResult() {
            // given
            Money a = Money.of(1000);
            Money b = Money.of(3000);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> a.minus(b));
        }

        @DisplayName("isGreaterThanOrEqual: 크거나 같으면 true를 반환한다.")
        @Test
        void isGreaterThanOrEqual() {
            // given
            Money big = Money.of(5000);
            Money small = Money.of(3000);
            Money same = Money.of(5000);

            // when & then
            assertThat(big.isGreaterThanOrEqual(small)).isTrue();
            assertThat(big.isGreaterThanOrEqual(same)).isTrue();
            assertThat(small.isGreaterThanOrEqual(big)).isFalse();
        }
    }

    @DisplayName("VO 동등성을 확인할 때,")
    @Nested
    class Equality {

        @DisplayName("같은 금액이면 동일한 객체로 간주한다.")
        @Test
        void equalByValue() {
            // given
            Money a = Money.of(1000);
            Money b = Money.of(1000);

            // when & then
            assertThat(a).isEqualTo(b);
        }
    }
}
