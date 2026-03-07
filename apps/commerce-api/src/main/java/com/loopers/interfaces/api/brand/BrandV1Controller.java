package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandFacade;
import com.loopers.application.brand.BrandInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandV1Controller implements BrandV1ApiSpec {

    private final BrandFacade brandFacade;

    @PostMapping
    @Override
    public ApiResponse<BrandV1Dto.BrandResponse> register(
            @RequestBody BrandV1Dto.RegisterRequest request
    ) {
        BrandInfo info = brandFacade.register(request.name(), request.description(), request.imageUrl());
        return ApiResponse.success(
                new BrandV1Dto.BrandResponse(
                        info.id(), info.name(), info.description(), info.imageUrl()
                )
        );
    }

    @GetMapping("/{id}")
    @Override
    public ApiResponse<BrandV1Dto.BrandResponse> getById(
            @PathVariable Long id
    ) {
        BrandInfo info = brandFacade.getById(id);
        return ApiResponse.success(
                new BrandV1Dto.BrandResponse(
                        info.id(), info.name(), info.description(), info.imageUrl()
                )
        );
    }
}
