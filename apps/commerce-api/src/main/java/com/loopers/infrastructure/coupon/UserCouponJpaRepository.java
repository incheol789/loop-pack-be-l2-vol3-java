package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.UserCouponModel;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserCouponJpaRepository extends JpaRepository<UserCouponModel, Long> {
    List<UserCouponModel> findAllByMemberId(Long memberId);
    Page<UserCouponModel> findAllByCouponId(Long couponId, Pageable pageable);
}
