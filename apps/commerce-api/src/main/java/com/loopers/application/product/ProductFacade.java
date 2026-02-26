package com.loopers.application.product;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final BrandService brandService;
    private final ProductLikeService productLikeService;

    @Transactional
    public ProductInfo register(Long brandId, String name, String description, int price, int stockQuantity, String imageUrl) {
        brandService.getById(brandId);
        ProductModel product = productService.register(brandId, name, description, price, stockQuantity, imageUrl);
        return ProductInfo.from(product);
    }

    @Transactional(readOnly = true)
    public ProductDetailInfo getById(Long id) {
        ProductModel product = productService.getById(id);
        BrandModel brand = brandService.getById(product.getBrandId());
        long likeCount = productLikeService.countByProductId(id);
        return ProductDetailInfo.of(product, brand, likeCount);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailInfo> getProducts(ProductSortType sortType) {
        List<ProductModel> products = productService.getAll();
        return buildAndSort(products, sortType);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailInfo> getProductsByBrandId(Long brandId, ProductSortType sortType) {
        List<ProductModel> products = productService.getByBrandId(brandId);
        return buildAndSort(products, sortType);
    }

    private List<ProductDetailInfo> buildAndSort(List<ProductModel> products, ProductSortType sortType) {
        return products.stream()
                .map(product -> {
                    BrandModel brand = brandService.getById(product.getBrandId());
                    long likeCount = productLikeService.countByProductId(product.getId());
                    return ProductDetailInfo.of(product, brand, likeCount);
                })
                .sorted(sortType.getComparator())
                .toList();
    }
}
