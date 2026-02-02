package com.loopers.interfaces.api.member;

import java.time.LocalDate;

public class MemberV1Dto {

	public record RegisterRequest(
			String loginId,
			String password,
			String name,
			LocalDate birthDate,
			String email
	) {}
}
