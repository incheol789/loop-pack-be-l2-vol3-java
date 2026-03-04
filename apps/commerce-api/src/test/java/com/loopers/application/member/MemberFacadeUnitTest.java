package com.loopers.application.member;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.PointService;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberFacadeUnitTest {

    @Mock
    private MemberService memberService;

    @Mock
    private PointService pointService;

    @InjectMocks
    private MemberFacade memberFacade;

    @DisplayName("회원가입할 때,")
    @Nested
    class Register {

        @DisplayName("정상 흐름이면, 회원 등록 후 포인트가 생성된다.")
        @Test
        void registerSuccess() {
            // given
            MemberModel member = new MemberModel("testuser", "password1!@", "홍길동",
                    LocalDate.of(2000, 6, 5), "test@example.com");
            ReflectionTestUtils.setField(member, "id", 1L);

            when(memberService.register("testuser", "password1!@", "홍길동",
                    LocalDate.of(2000, 6, 5), "test@example.com"))
                    .thenReturn(member);

            // when
            memberFacade.register("testuser", "password1!@", "홍길동",
                    LocalDate.of(2000, 6, 5), "test@example.com");

            // then
            verify(pointService).createPoint(1L);
        }

        @DisplayName("중복 loginId이면, CONFLICT 예외가 발생한다.")
        @Test
        void failWithDuplicateLoginId() {
            // given
            when(memberService.register("testuser", "password1!@", "홍길동",
                    LocalDate.of(2000, 6, 5), "test@example.com"))
                    .thenThrow(new CoreException(ErrorType.CONFLICT, "이미 존재하는 loginId입니다."));

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    memberFacade.register("testuser", "password1!@", "홍길동",
                            LocalDate.of(2000, 6, 5), "test@example.com")
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayName("내 정보를 조회할 때,")
    @Nested
    class GetMyInfo {

        @DisplayName("정상 흐름이면, 회원 정보와 포인트가 조합된 MemberInfo가 반환된다.")
        @Test
        void getMyInfoSuccess() {
            // given
            MemberModel member = new MemberModel("testuser", "password1!@", "홍길동",
                    LocalDate.of(2000, 6, 5), "test@example.com");
            ReflectionTestUtils.setField(member, "id", 1L);

            PointModel point = new PointModel(1L);
            point.charge(Money.of(10000));

            when(memberService.getMyInfo("testuser", "password1!@")).thenReturn(member);
            when(pointService.getByMemberId(1L)).thenReturn(point);

            // when
            MemberInfo result = memberFacade.getMyInfo("testuser", "password1!@");

            // then
            assertAll(
                    () -> assertThat(result.loginId()).isEqualTo("testuser"),
                    () -> assertThat(result.maskedName()).isEqualTo("홍길*"),
                    () -> assertThat(result.birthDate()).isEqualTo(LocalDate.of(2000, 6, 5)),
                    () -> assertThat(result.email()).isEqualTo("test@example.com"),
                    () -> assertThat(result.point()).isEqualTo(Money.of(10000))
            );
        }

        @DisplayName("비밀번호가 틀리면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithWrongPassword() {
            // given
            when(memberService.getMyInfo("testuser", "wrong1234!@"))
                    .thenThrow(new CoreException(ErrorType.BAD_REQUEST, "비밀번호가 일치하지 않습니다."));

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    memberFacade.getMyInfo("testuser", "wrong1234!@")
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트를 충전할 때,")
    @Nested
    class ChargePoint {

        @DisplayName("정상 흐름이면, 회원 인증 후 포인트가 충전된다.")
        @Test
        void chargePointSuccess() {
            // given
            MemberModel member = new MemberModel("testuser", "password1!@", "홍길동",
                    LocalDate.of(2000, 6, 5), "test@example.com");
            ReflectionTestUtils.setField(member, "id", 1L);

            when(memberService.getMyInfo("testuser", "password1!@")).thenReturn(member);

            // when
            memberFacade.chargePoint("testuser", "password1!@", 5000);

            // then
            verify(pointService).charge(1L, Money.of(5000));
        }
    }

    @DisplayName("비밀번호를 변경할 때,")
    @Nested
    class ChangePassword {

        @DisplayName("정상 흐름이면, 비밀번호가 변경된다.")
        @Test
        void changePasswordSuccess() {
            // when
            memberFacade.changePassword("testuser", "password1!@", "newpass1!@#");

            // then
            verify(memberService).changePassword("testuser", "password1!@", "newpass1!@#");
        }

        @DisplayName("현재 비밀번호와 동일하면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithSamePassword() {
            // given
            doThrow(new CoreException(ErrorType.BAD_REQUEST, "현재 비밀번호와 동일한 비밀번호는 사용할 수 없습니다."))
                    .when(memberService).changePassword("testuser", "password1!@", "password1!@");

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    memberFacade.changePassword("testuser", "password1!@", "password1!@")
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
