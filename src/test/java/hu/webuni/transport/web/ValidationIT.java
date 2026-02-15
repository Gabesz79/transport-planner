package hu.webuni.transport.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ValidationIT {

	@Autowired
	WebTestClient webTestClient;
	
	@Test
	void createAddress_shouldReturnValidationError_whenDtoInvalid() {
		//itt üres body legyen, így a Dto mezők sértik a @NotBlank valdációkat:
		webTestClient.post()
			.uri("/api/addresses")
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
	
}
