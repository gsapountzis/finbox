package com.xm.finbox.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserLogoutTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private static final String AUTH = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI5NDM3MTU0OS05Mzg1LTQ2MWUtYmFhMi1iNGViYWM2MjU2MzMiLCJzdWIiOiJmb29AYmFyLmNvbSIsImlhdCI6MTUxODY1Mzg0Nn0.gwgrgln_31SjsHHBSb2z-YQR2xjMXUJbwgXVB6NVNuP7cxxmTpEe8wdfg0Knp4qt5a9cekGBx7EEWxBkE1udLA";

	@Test
	public void authorizedUserShouldLogout() {
		HttpHeaders headers = getJsonHeaders();

		// Register
		UserRegistrationForm user = new UserRegistrationForm();
		user.setFirstName("Foo");
		user.setLastName("Bar");
		user.setEmail("valid-logout@mail.com");
		user.setPassword("foobar");
		HttpEntity<UserRegistrationForm> registrationRequest = new HttpEntity<>(user, headers);

		ResponseEntity<Void> registrationResponse = restTemplate.exchange("/users", HttpMethod.POST, registrationRequest, Void.class);
		assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Login
		UserLoginForm credentials = new UserLoginForm();
		credentials.setEmail("valid-logout@mail.com");
		credentials.setPassword("foobar");
		HttpEntity<UserLoginForm> loginRequest = new HttpEntity<>(credentials, headers);

		ResponseEntity<Void> loginResponse = restTemplate.exchange("/auth", HttpMethod.POST, loginRequest, Void.class);
		String authorization = loginResponse.getHeaders().get("Authorization").get(0);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(authorization).isNotBlank().startsWith("Bearer");

		// Logout
		headers.set("Authorization", authorization);
		HttpEntity<Void> logoutRequest = new HttpEntity<>(headers);

		ResponseEntity<Void> logoutResponse = restTemplate.exchange("/logout", HttpMethod.POST, logoutRequest, Void.class);
		assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void unauthorizedUserShouldNotLogout() {
		HttpHeaders headers = getJsonHeaders();
		headers.set("Authorization", AUTH);
		HttpEntity<Void> logoutRequest = new HttpEntity<>(headers);

		ResponseEntity<Void> logoutResponse = restTemplate.exchange("/logout", HttpMethod.POST, logoutRequest, Void.class);
		assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private HttpHeaders getJsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
