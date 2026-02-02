package com.loopers.interfaces.api;

import com.loopers.domain.member.MemberService;
import com.loopers.interfaces.api.member.MemberV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberV1ApiE2ETest {

	private static final String ENDPOINT_REGISTER = "/api/v1/members";

	private final TestRestTemplate testRestTemplate;
	private final MemberService memberService;
	private final DatabaseCleanUp databaseCleanUp;

	@Autowired
	public MemberV1ApiE2ETest(
			TestRestTemplate testRestTemplate,
			MemberService memberService,
			DatabaseCleanUp databaseCleanUp
	) {
		this.testRestTemplate = testRestTemplate;
		this.memberService = memberService;
		this.databaseCleanUp = databaseCleanUp;
	}

	@AfterEach
	void tearDown() {
		databaseCleanUp.truncateAllTables();
	}

	@DisplayName("POST /api/v1/members")
	@Nested
	class Register {

		@DisplayName("유효한 정보로 회원가입하면, 200 응답을 받는다.")
		@Test
		void registerSuccess() {
			// given
			MemberV1Dto.RegisterRequest request = new MemberV1Dto.RegisterRequest(
					"testuser", "password1!@", "홍길동", LocalDate.of(2000, 6, 5), "test@example.com"
			);

			// when
			ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
			ResponseEntity<ApiResponse<Void>> response = testRestTemplate.exchange(
					ENDPOINT_REGISTER, HttpMethod.POST, new HttpEntity<>(request), responseType
			);

			// then
			assertAll(
					() -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
					() -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS)
			);
		}

		@DisplayName("중복된 loginId로 회원가입하면, 409 CONFLICT 응답을 받는다.")
		@Test
		void failWithDuplicateLoginId() {
			// given
			memberService.register("testuser", "password1!@", "홍길동", LocalDate.of(2000, 6, 5), "test@example.com");

			MemberV1Dto.RegisterRequest request = new MemberV1Dto.RegisterRequest(
					"testuser", "other1234!@", "김철수", LocalDate.of(1995, 3, 10), "other@example.com"
			);

			// when
			ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
			ResponseEntity<ApiResponse<Void>> response = testRestTemplate.exchange(
					ENDPOINT_REGISTER, HttpMethod.POST, new HttpEntity<>(request), responseType
			);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		}
	}
}
