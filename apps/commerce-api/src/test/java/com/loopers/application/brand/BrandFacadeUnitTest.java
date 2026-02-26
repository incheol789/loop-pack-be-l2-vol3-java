package com.loopers.application.brand;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandFacadeUnitTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandFacade brandFacade;

    @DisplayName("브랜드를 등록할 때,")
    @Nested
    class Register {

        @DisplayName("유효한 정보이면, 브랜드가 등록되고 BrandInfo가 반환된다.")
        @Test
        void registerSuccess() {
            // given
            BrandModel brand = new BrandModel("나이키", "스포츠 브랜드", "https://example.com/nike.png");
            ReflectionTestUtils.setField(brand, "id", 1L);
            when(brandService.register("나이키", "스포츠 브랜드", "https://example.com/nike.png"))
                    .thenReturn(brand);

            // when
            BrandInfo result = brandFacade.register("나이키", "스포츠 브랜드", "https://example.com/nike.png");

            // then
            assertAll(
                    () -> assertThat(result.id()).isEqualTo(1L),
                    () -> assertThat(result.name()).isEqualTo("나이키"),
                    () -> assertThat(result.description()).isEqualTo("스포츠 브랜드"),
                    () -> assertThat(result.imageUrl()).isEqualTo("https://example.com/nike.png")
            );
        }
    }

    @DisplayName("브랜드를 조회할 때,")
    @Nested
    class GetById {

        @DisplayName("존재하는 브랜드이면, BrandInfo가 반환된다.")
        @Test
        void getByIdSuccess() {
            // given
            BrandModel brand = new BrandModel("아디다스", "스포츠 브랜드", "https://example.com/adidas.png");
            ReflectionTestUtils.setField(brand, "id", 2L);
            when(brandService.getById(2L)).thenReturn(brand);

            // when
            BrandInfo result = brandFacade.getById(2L);

            // then
            assertAll(
                    () -> assertThat(result.id()).isEqualTo(2L),
                    () -> assertThat(result.name()).isEqualTo("아디다스")
            );
        }

        @DisplayName("존재하지 않는 브랜드이면, NOT_FOUND 예외가 발생한다.")
        @Test
        void failWithNotFound() {
            // given
            when(brandService.getById(999L))
                    .thenThrow(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다."));

            // when
            CoreException result = assertThrows(CoreException.class, () ->
                    brandFacade.getById(999L)
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
