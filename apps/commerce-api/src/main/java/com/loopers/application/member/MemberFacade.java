package com.loopers.application.member;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.PointService;
import com.loopers.domain.vo.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class MemberFacade {

	private final MemberService memberService;
    private final PointService pointService;

	@Transactional
	public void register(String loginId, String password, String name, LocalDate birthDate, String email) {
		MemberModel member = memberService.register(loginId, password, name, birthDate, email);
		pointService.createPoint(member.getId());
	}

	@Transactional(readOnly = true)
	public MemberInfo getMyInfo(String loginId, String password) {
		MemberModel member = memberService.getMyInfo(loginId, password);
        PointModel point = pointService.getByMemberId(member.getId());
        return MemberInfo.of(member, point.getBalanceMoney());
	}

    @Transactional
    public void chargePoint(String loginId, String password, long amount) {
        MemberModel member = memberService.getMyInfo(loginId, password);
        pointService.charge(member.getId(), Money.of(amount));
    }

	@Transactional
	public void changePassword(String loginId, String currentPassword, String newPassword) {
		memberService.changePassword(loginId, currentPassword, newPassword);
	}
}
