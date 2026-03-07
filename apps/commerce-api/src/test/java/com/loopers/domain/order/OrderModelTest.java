package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderModelTest {

    @DisplayName("주문 항목을 생성할 때,")
    @Nested
    class CreateOrderItem {

        @DisplayName("유효한 정보면, 정상적으로 생성된다.")
        @Test
        void createOrderItemSuccess() {
            // given & when
            OrderItemModel item = new OrderItemModel(1L, "에어맥스", "나이키", 129000, 2);

            // then
            assertAll(
                    () -> assertThat(item.getProductId()).isEqualTo(1L),
                    () -> assertThat(item.getProductName()).isEqualTo("에어맥스"),
                    () -> assertThat(item.getBrandName()).isEqualTo("나이키"),
                    () -> assertThat(item.getProductPrice()).isEqualTo(129000),
                    () -> assertThat(item.getQuantity()).isEqualTo(2),
                    () -> assertThat(item.getTotalAmount()).isEqualTo(258000)
            );
        }

        @DisplayName("상품 ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNullProductId() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new OrderItemModel(null, "에어맥스", "나이키", 129000, 2)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("상품명이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNullProductName() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new OrderItemModel(1L, null, "나이키", 129000, 2)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("가격이 음수이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNegativePrice() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new OrderItemModel(1L, "에어맥스", "나이키", -1, 2)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("수량이 0이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithZeroQuantity() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new OrderItemModel(1L, "에어맥스", "나이키", 129000, 0)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("주문 항목에 주문 ID를 할당할 때,")
    @Nested
    class AssignOrderId {

        @DisplayName("주문 ID가 할당된다.")
        @Test
        void assignOrderIdSuccess() {
            // given
            OrderItemModel item = new OrderItemModel(1L, "에어맥스", "나이키", 129000, 2);

            // when
            item.assignOrderId(100L);

            // then
            assertThat(item.getOrderId()).isEqualTo(100);
        }
    }

    @DisplayName("주문을 생성할 때,")
    @Nested
    class CreateOrder {

        @DisplayName("유효한 정보면, 정상적으로 생성된다.")
        @Test
        void createOrderSuccess() {
            // given
            Long memberId = 1L;
            List<OrderItemModel> items = List.of(
                    new OrderItemModel(1L, "에어맥스", "나이키", 129000, 2),
                    new OrderItemModel(2L, "에어포스", "나이키", 109000, 1)
            );
            
            // when
            OrderModel order = new OrderModel(memberId, items, null, 0);

            // then
            assertAll(
                    () -> assertThat(order.getMemberId()).isEqualTo(memberId),
                    () -> assertThat(order.getTotalAmount()).isEqualTo(367000)
            );
        }

        @DisplayName("회원 ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNullMemberId() {
            // given
            List<OrderItemModel> items = List.of(
                    new OrderItemModel(1L, "에어맥스", "나이키", 129000, 2)
            );

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    new OrderModel(null, items, null, 0)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("주문 항목이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithNullItems() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new OrderModel(1L, null, null, 0)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("주문 항목이 비어있으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithEmptyItems() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    new OrderModel(1L, Collections.emptyList(), null, 0)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
