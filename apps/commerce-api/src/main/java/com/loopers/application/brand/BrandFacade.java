package com.loopers.application.brand;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BrandFacade {

    private final BrandService brandService;

    @Transactional
    public BrandInfo register(String name, String description, String imageUrl) {
        BrandModel brand = brandService.register(name, description, imageUrl);
        return BrandInfo.from(brand);
    }

    @Transactional(readOnly = true)
    public BrandInfo getById(Long id) {
        BrandModel brand = brandService.getById(id);
        return BrandInfo.from(brand);
    }
}
