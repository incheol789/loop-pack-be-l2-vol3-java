package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order V1 API", description = "주문 API 입니다.")
public interface OrderV1ApiSpec {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다. 재고 차감 및 포인트 차감이 수행됩니다.")
    ApiResponse<OrderV1Dto.OrderResponse> createOrder(
            @Parameter(description = "로그인 ID") String loginId,
            @Parameter(description = "로그인 비밀번호") String loginPw,
            OrderV1Dto.CreateRequest request
    );

    @Operation(summary = "주문 조회", description = "ID로 주문을 조회합니다.")
    ApiResponse<OrderV1Dto.OrderResponse> getById(@Parameter(description = "주문 ID") Long id);

    @Operation(summary = "내 주문 목록 조회", description = "로그인한 회원의 주문 목록을 조회합니다.")
    ApiResponse<OrderV1Dto.OrderListResponse> getMyOrders(
            @Parameter(description = "로그인 ID") String loginId,
            @Parameter(description = "로그인 비밀번호") String loginPw
    );
}
