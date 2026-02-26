package com.loopers.interfaces.api.member;

import com.loopers.domain.vo.Money;

import java.time.LocalDate;

public class MemberV1Dto {

    public record RegisterRequest(
            String loginId,
            String password,
            String name,
            LocalDate birthDate,
            String email
    ) {
    }

    public record MyInfoResponse(
            String loginId,
            String maskedName,
            LocalDate birthDate,
            String email,
            Money point
    ) {
    }

    public record ChargePointRequest(
            long amount
    ) {

    }

    public record ChangePasswordRequest(
            String newPassword
    ) {
    }
}
