package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @PostMapping
    @Override
    public ApiResponse<OrderV1Dto.OrderResponse> createOrder(
            @RequestHeader("X-Loopers-LoginId") String loginId,
            @RequestHeader("X-Loopers-LoginPw") String loginPw,
            @RequestBody OrderV1Dto.CreateRequest request
    ) {
        List<OrderFacade.OrderItemRequest> itemRequests = request.items().stream()
                .map(item -> new OrderFacade.OrderItemRequest(item.productId(), item.quantity()))
                .toList();

        OrderInfo info = orderFacade.createOrder(loginId, loginPw, itemRequests);
        return ApiResponse.success(toResponse(info));
    }

    @GetMapping("/{id}")
    @Override
    public ApiResponse<OrderV1Dto.OrderResponse> getById(@PathVariable Long id) {
        OrderInfo info = orderFacade.getById(id);
        return ApiResponse.success(toResponse(info));
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<OrderV1Dto.OrderListResponse> getMyOrders(
            @RequestHeader("X-Loopers-LoginId") String loginId,
            @RequestHeader("X-Loopers-LoginPw") String loginPw
    ) {
        List<OrderInfo> orders = orderFacade.getByMember(loginId, loginPw);
        List<OrderV1Dto.OrderResponse> responses = orders.stream()
                .map(this::toResponse)     // 각 OrderInfo를 OrderResponse로 변환
                .toList();
        return ApiResponse.success(new OrderV1Dto.OrderListResponse(responses));
    }

    private OrderV1Dto.OrderResponse toResponse(OrderInfo info) {
        List<OrderV1Dto.OrderItemResponse> items = info.items().stream()
                .map(item -> new OrderV1Dto.OrderItemResponse(
                        item.productId(),
                        item.productName(),
                        item.brandName(),
                        item.productPrice(),
                        item.quantity(),
                        item.totalAmount()
                ))
                .toList();

        return new OrderV1Dto.OrderResponse(
                info.id(), info.memberId(), info.totalAmount(), items
        );
    }
}
