package hu.webuni.transport.web;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import hu.webuni.transport.dto.LoginResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ValidationIT {

	@Autowired
	WebTestClient webTestClient;
	
	String addressJwt; 
	
	@BeforeEach
	void initJwt() {
		addressJwt = login("addressmgr", "pass123");
 	}
	
	@Test
	void createAddress_shouldReturnForbidden_whenNoToken() {
		webTestClient.post()
			.uri("/api/addresses")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue("""
					{
						"country": "Hungary",
						"zip": "1041",
						"city": "Budapest",
						"street": "Fo utca",
						"houseNumber": "15"
					}
					""")
			.exchange()
			.expectStatus().isForbidden();
		
	}
	
	@Test
	void createAddress_shouldReturnValidationError_whenDtoInvalid() {
		//itt üres a body, így a Dto mezők sértik a @NotBlank valdációkat (TOKENNEL):
		webTestClient.post()
			.uri("/api/addresses")
			.headers(headers -> headers.setBearerAuth(addressJwt))
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue("""
					{
						"country": "",
						"zip": "",
						"city": "",
						"street": "",
						"houseNumber": ""
					}
					""")
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody()
			.jsonPath("$.errorCode").isEqualTo("VALIDATION_ERROR")
			.jsonPath("$.errorMessage").isEqualTo("Validation failed");
	}
	
	private String login(String username, String password) {
		LoginResponseDto loginResponseDto = webTestClient.post()
				.uri("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("username", username, "password", password))
				.exchange()
				.expectStatus().isOk()
				.expectBody(LoginResponseDto.class)
				.returnResult()
				.getResponseBody();
				
		return loginResponseDto.getToken();
	}
	
}
