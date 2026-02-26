package com.loopers.interfaces.api.like;

public class LikeV1Dto {

    public record CountResponse(
            Long productId,
            long count
    ) {

    }
}
