package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("주문을 생성할 때,")
    @Nested
    class CreateOrder {

        @DisplayName("유효한 정보면, 주문과 주문 항목이 저장된다.")
        @Test
        void createOrderSuccess() {
            // given
            Long memberId = 1L;
            List<OrderItemModel> items = List.of(
                    new OrderItemModel(1L, "에어맥스", "나이키", 129000, 2),
                    new OrderItemModel(2L, "에어포스", "나이키", 109000, 1)
            );

            when(orderRepository.save(any(OrderModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(orderRepository.saveItem(any(OrderItemModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            OrderModel result = orderService.createOrder(memberId, items, null, 0);

            // then
            assertAll(
                    () -> assertThat(result.getMemberId()).isEqualTo(memberId),
                    () -> assertThat(result.getTotalAmount()).isEqualTo(129000 * 2 + 109000)
            );
            verify(orderRepository, times(1)).save(any(OrderModel.class));
            verify(orderRepository, times(2)).saveItem(any(OrderItemModel.class));
        }
    }

    @DisplayName("주문을 조회할 때,")
    @Nested
    class GetById {

        @DisplayName("존재하는 ID면, 주문이 반환된다.")
        @Test
        void getByIdSuccess() {
            // given
            List<OrderItemModel> items = List.of(new OrderItemModel(1L, "에어맥스", "나이키", 129000, 1));
            OrderModel order = new OrderModel(1L, items, null, 0);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            // when
            OrderModel result = orderService.getById(1L);

            // then
            assertThat(result.getMemberId()).isEqualTo(1L);
        }

        @DisplayName("존재하지 않는 ID면, NOT_FOUND 예외가 발생한다.")
        @Test
        void failWithNotFoundId() {
            // given
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    orderService.getById(999L)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("회원의 주문 목록을 조회할 때,")
    @Nested
    class GetByMemberId {

        @DisplayName("해당 회원의 주문 목록이 반환된다.")
        @Test
        void getByMemberIdSuccess() {
            // given
            List<OrderItemModel> items = List.of(
                    new OrderItemModel(1L, "에어맥스", "나이키", 129000, 1)
            );
            List<OrderModel> orders = List.of(new OrderModel(1L, items, null, 0));
            when(orderRepository.findAllByMemberId(1L)).thenReturn(orders);

            // when
            List<OrderModel> result = orderService.getByMemberId(1L);

            // then
            assertThat(result).hasSize(1);
        }
    }
}
