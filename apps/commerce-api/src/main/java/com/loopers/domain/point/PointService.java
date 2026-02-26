package com.loopers.domain.point;

import com.loopers.domain.vo.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public PointModel createPoint(Long memberId) {
        PointModel point = new PointModel(memberId);
        return pointRepository.save(point);
    }

    @Transactional(readOnly = true)
    public PointModel getByMemberId(Long memberId) {
        return pointRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "포인트 정보가 존재하지 않습니다."));
    }

    @Transactional
    public void charge(Long memberId, Money amount) {
        PointModel point = getByMemberId(memberId);
        point.charge(amount);
    }

    @Transactional
    public void use(Long memberId, Money amount) {
        PointModel point = getByMemberId(memberId);
        point.use(amount);
    }

}
