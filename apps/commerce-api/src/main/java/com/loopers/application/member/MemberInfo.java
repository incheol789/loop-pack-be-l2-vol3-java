package com.loopers.application.member;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.vo.Money;

import java.time.LocalDate;

public record MemberInfo(
		String loginId,
		String maskedName,
		LocalDate birthDate,
		String email,
        Money point
) {
	public static MemberInfo of(MemberModel model, Money point) {
		return new MemberInfo(
				model.getLoginId(),
				model.getMaskedName(),
				model.getBirthDate(),
				model.getEmail(),
                point
		);
	}
}
