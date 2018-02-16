package com.xm.finbox.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.xm.finbox.user.UserLoginForm;
import com.xm.finbox.user.UserRegistrationForm;

public class FileUserUtil {

	public static HttpHeaders getJsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public static String user(TestRestTemplate restTemplate, String email) {
		HttpHeaders headers = getJsonHeaders();

		// Register
		UserRegistrationForm user = new UserRegistrationForm();
		user.setFirstName("Foo");
		user.setLastName("Bar");
		user.setEmail(email);
		user.setPassword("foobar");
		HttpEntity<UserRegistrationForm> registrationRequest = new HttpEntity<>(user, headers);

		ResponseEntity<Void> registrationResponse = restTemplate.exchange("/users", HttpMethod.POST, registrationRequest, Void.class);
		assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Login
		UserLoginForm credentials = new UserLoginForm();
		credentials.setEmail(email);
		credentials.setPassword("foobar");
		HttpEntity<UserLoginForm> loginRequest = new HttpEntity<>(credentials, headers);

		ResponseEntity<Void> loginResponse = restTemplate.exchange("/auth", HttpMethod.POST, loginRequest, Void.class);
		String authorization = loginResponse.getHeaders().get("Authorization").get(0);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(authorization).isNotBlank().startsWith("Bearer");

		return authorization;
	}

}
