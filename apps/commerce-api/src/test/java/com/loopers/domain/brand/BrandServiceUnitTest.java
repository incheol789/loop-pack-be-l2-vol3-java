package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceUnitTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @DisplayName("브랜드를 등록할 때,")
    @Nested
    class Register {

        @DisplayName("유효한 이름이면, 정상적으로 등록된다")
        @Test
        void registerSuccess() {
            // given
            String name = "나이키";
            String description = "스포츠 브랜드";
            String imageUrl = "https://example.com/nike.png";
            when(brandRepository.save(any(BrandModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            BrandModel result = brandService.register(name, description, imageUrl);

            // then
            assertAll(
                    () -> assertThat(result.getName()).isEqualTo(name),
                    () -> assertThat(result.getDescription()).isEqualTo(description),
                    () -> assertThat(result.getImageUrl()).isEqualTo(imageUrl)
            );
            verify(brandRepository, times(1)).save(any(BrandModel.class));
        }

        @DisplayName("이름이 null이면, save가 호출되지 않고 예외가 발생한다.")
        @Test
        void failWithNullName() {
            // given & when
            CoreException result = assertThrows(CoreException.class, () ->
                    brandService.register(null, "설명", "url")
            );

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            verify(brandRepository, never()).save(any(BrandModel.class));
        }
    }

    @DisplayName("브랜드를 조회할 때,")
    @Nested
    class GetById {

        @DisplayName("존재하는 ID면, 브랜드가 반환된다.")
        @Test
        void getByIdSuccess() {
            // given
            Long id = 1L;
            BrandModel brand = new BrandModel("나이키", "스포츠 브랜드", "https://example.com/nike.png");
            when(brandRepository.findById(id)).thenReturn(Optional.of(brand));

            // when
            BrandModel result = brandService.getById(id);

            // then
            assertThat(result.getName()).isEqualTo("나이키");
        }

        @DisplayName("존재하지 않는 ID면, NOT_FOUND가 반환된다.")
        @Test
        void failWithNotFoundId() {
            // given
            when(brandRepository.findById(999L)).thenReturn(Optional.empty());

            // when
            CoreException result = assertThrows(CoreException.class, () -> brandService.getById(999L));

            // then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
