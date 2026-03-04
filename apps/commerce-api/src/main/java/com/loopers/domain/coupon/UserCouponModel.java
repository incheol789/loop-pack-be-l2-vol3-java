package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "user_coupon")
@Getter
public class UserCouponModel extends BaseEntity {

    @Column(nullable = false)
    private Long couponId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserCouponStatus status;

    private ZonedDateTime usedAt;

    @Version
    private Long version;

    protected UserCouponModel() {
    }

    public UserCouponModel(Long couponId, Long memberId) {
        if (couponId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 ID는 필수입니다.");
        }
        if (memberId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "회원 ID는 필수입니다.");
        }
        this.couponId = couponId;
        this.memberId = memberId;
        this.status = UserCouponStatus.AVAILABLE;
    }

    public void use() {
        if (this.status != UserCouponStatus.AVAILABLE) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용할 수 없는 쿠폰입니다.");
        }
        this.status = UserCouponStatus.USED;
        this.usedAt = ZonedDateTime.now();
    }

    public void validateOwner(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "본인의 쿠폰만 사용할 수 있습니다.");
        }
    }

    // 만료 확인
    public boolean isExpired() {
        return this.status == UserCouponStatus.EXPIRED;
    }
}
