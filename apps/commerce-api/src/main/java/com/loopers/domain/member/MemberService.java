package com.loopers.domain.member;

import com.loopers.support.crypto.PasswordEncoder;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	public MemberModel register(String loginId, String password, String name, LocalDate birthDate, String email) {
		memberRepository.findByLoginId(loginId)
				.ifPresent(member -> {
					throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 loginId입니다.");
				});

		MemberModel member = new MemberModel(loginId, password, name, birthDate, email);
		member.applyEncodedPassword(PasswordEncoder.encode(password));
		return memberRepository.save(member);
	}
}
