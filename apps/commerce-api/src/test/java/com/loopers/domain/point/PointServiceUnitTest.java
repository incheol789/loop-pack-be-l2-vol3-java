package com.loopers.domain.point;

import com.loopers.domain.vo.Money;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceUnitTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

    @DisplayName("포인트를 생성할 때,")
    @Nested
    class CreatePoint {

        @DisplayName("유효한 회원 ID면, 포인트가 생성된다.")
        @Test
        void createPointSuccess() {
            // given
            Long memberId = 1L;
            when(pointRepository.save(any(PointModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            PointModel result = pointService.createPoint(memberId);

            // then
            assertAll(
                    () -> assertThat(result.getMemberId()).isEqualTo(memberId),
                    () -> assertThat(result.getBalanceMoney()).isEqualTo(Money.of(0))
            );
            verify(pointRepository, times(1)).save(any(PointModel.class));
        }
    }

    @DisplayName("포인트를 충전할 때,")
    @Nested
    class Charge {

        @DisplayName("존재하는 회원이면, 포인트가 충전된다.")
        @Test
        void chargeSuccess() {
            // given
            Long memberId = 1L;
            PointModel point = new PointModel(memberId);
            when(pointRepository.findByMemberId(memberId))
                    .thenReturn(Optional.of(point));

            // when
            pointService.charge(memberId, Money.of(10000));

            // then
            assertThat(point.getBalanceMoney()).isEqualTo(Money.of(10000));
        }

        @DisplayName("존재하지 않는 회원이면, NOT_FOUND 예외가 발생한다.")
        @Test
        void failWithNotFoundMember() {
            // given
            when(pointRepository.findByMemberId(999L))
                    .thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    pointService.charge(999L, Money.of(10000))
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

    }

    @DisplayName("포인트를 사용할 때,")
    @Nested
    class Use {
        
        @DisplayName("충분한 잔액이 있으면, 정상적으로 차감된다.")
        @Test
        void useSuccess() {
            // given
            Long memberId = 1L;
            PointModel point = new PointModel(memberId);
            point.charge(Money.of(10000));
            when(pointRepository.findByMemberId(memberId))
                    .thenReturn(Optional.of(point));

            // when
            pointService.use(memberId, Money.of(3000));

            // then
            assertThat(point.getBalanceMoney()).isEqualTo(Money.of(7000));
        }

        @DisplayName("존재하지 않는 회원이면, NOT_FOUND 예외가 발생한다.")
        @Test
        void failWithNotFoundMember() {
            // given
            when(pointRepository.findByMemberId(999L))
                    .thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    pointService.use(999L, Money.of(1000))
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("잔액이 부족하면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithInsufficientBalance() {
            // given
            Long memberId = 1L;
            PointModel point = new PointModel(memberId);
            point.charge(Money.of(1000));
            when(pointRepository.findByMemberId(memberId))
                    .thenReturn(Optional.of(point));

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    pointService.use(memberId, Money.of(5000)));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("회원의 포인트를 조회할 때,")
    @Nested
    class GetByMemberId {

        @DisplayName("존재하는 회원이면, 포인트가 반환된다.")
        @Test
        void getByMemberIdSuccess() {
            // given
            Long memberId = 1L;
            PointModel point = new PointModel(memberId);
            when(pointRepository.findByMemberId(memberId))
                    .thenReturn(Optional.of(point));

            // when
            PointModel result = pointService.getByMemberId(memberId);

            // then
            assertThat(result.getMemberId()).isEqualTo(memberId);
        }

        @DisplayName("존재하지 않는 회원이면, NOT_FOUND 예외가 발생한다.")
        @Test
        void failWithNotFoundMember() {
            // given
            when(pointRepository.findByMemberId(999L))
                    .thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    pointService.getByMemberId(999L)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}



