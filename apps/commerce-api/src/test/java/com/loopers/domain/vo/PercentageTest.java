package com.loopers.domain.vo;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PercentageTest {

    @DisplayName("Percentage를 생성할 때,")
    @Nested
    class Create {

        @DisplayName("1~100 범위이면 정상 생성된다.")
        @Test
        void createSuccess() {
            // given & when
            Percentage percentage = Percentage.of(10);

            // then
            assertThat(percentage.value()).isEqualTo(10);
        }

        @DisplayName("0이면 예외가 발생한다.")
        @Test
        void failWithZero() {
            // given & when & then
            assertThatThrownBy(() -> Percentage.of(0))
                    .isInstanceOf(CoreException.class);
        }

        @DisplayName("101이면 예외가 발생한다.")
        @Test
        void failWith101() {
            // given & when & then
            assertThatThrownBy(() -> Percentage.of(101))
                    .isInstanceOf(CoreException.class);
        }

        @DisplayName("음수이면 예외가 발생한다.")
        @Test
        void failWithNegative() {
            // given & when & then
            assertThatThrownBy(() -> Percentage.of(-1))
                    .isInstanceOf(CoreException.class);
        }
    }

    @DisplayName("할인 금액을 계산할 때,")
    @Nested
    class CalculateDiscount {

        @DisplayName("10% + 30000원 → 3000원")
        @Test
        void apply10Percent() {
            // given
            Percentage percentage = Percentage.of(10);
            Money price = Money.of(30000);

            // when
            Money discount = percentage.calculateDiscount(price);

            // then
            assertThat(discount.amount()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        }

        @DisplayName("50% + 10000원 → 5000원")
        @Test
        void apply50Percent() {
            // given
            Percentage percentage = Percentage.of(50);
            Money price = Money.of(10000);

            // when
            Money discount = percentage.calculateDiscount(price);

            // then
            assertThat(discount.amount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        }

        @DisplayName("100% + 5000원 → 5000원")
        @Test
        void apply100Percent() {
            // given
            Percentage percentage = Percentage.of(100);
            Money price = Money.of(5000);

            // when
            Money discount = percentage.calculateDiscount(price);

            // then
            assertThat(discount.amount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        }
    }

    @DisplayName("VO 동등성을 확인할 때,")
    @Nested
    class Equality {

        @DisplayName("같은 값이면 동일한 객체로 간주한다.")
        @Test
        void equalByValue() {
            // given
            Percentage a = Percentage.of(10);
            Percentage b = Percentage.of(10);

            // when & then
            assertThat(a).isEqualTo(b);
        }
    }
}
