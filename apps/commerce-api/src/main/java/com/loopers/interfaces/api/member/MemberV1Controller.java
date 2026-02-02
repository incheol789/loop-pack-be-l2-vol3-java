package com.loopers.interfaces.api.member;

import com.loopers.application.member.MemberFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberV1Controller implements MemberV1ApiSpec {

	private final MemberFacade memberFacade;

	@PostMapping
	@Override
	public ApiResponse<Void> register(@RequestBody MemberV1Dto.RegisterRequest request) {
		memberFacade.register(
				request.loginId(),
				request.password(),
				request.name(),
				request.birthDate(),
				request.email()
		);
		return ApiResponse.success(null);
	}
}
