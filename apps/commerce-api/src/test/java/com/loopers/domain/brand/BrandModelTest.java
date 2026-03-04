package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BrandModelTest {

    @DisplayName("브랜드를 생성할 때,")
    @Nested
    class Create {

        @DisplayName("유효한 이름이면, 정상적으로 생성된다.")
        @Test
        void createBrand() {
            // given
            String name = "나이키";
            String description = "스포츠 브랜드";
            String imageUrl = "https://example.com/nike.png";

            // when
            BrandModel brand = new BrandModel(name, description, imageUrl);

            // then
            assertAll(
                    () -> assertThat(brand.getName()).isEqualTo(name),
                    () -> assertThat(brand.getDescription()).isEqualTo(description),
                    () -> assertThat(brand.getImageUrl()).isEqualTo(imageUrl)
            );
        }

        @DisplayName("이름이 null이면, BAD_REQUEST를 반환한다.")
        @Test
        void failWithNullName() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () -> new BrandModel(null, "설명", "url"));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이름이 빈값이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void failWithEmptyName() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () -> new BrandModel(" ", "설명", "url"));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
