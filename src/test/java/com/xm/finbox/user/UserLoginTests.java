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
public class UserLoginTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void validUserShouldAuthenticate() {
		HttpHeaders headers = getJsonHeaders();

		// Register
		UserRegistrationForm user = new UserRegistrationForm();
		user.setFirstName("Foo");
		user.setLastName("Bar");
		user.setEmail("valid-login@mail.com");
		user.setPassword("foobar");
		HttpEntity<UserRegistrationForm> registrationRequest = new HttpEntity<>(user, headers);

		ResponseEntity<Void> registrationResponse = restTemplate.exchange("/users", HttpMethod.POST, registrationRequest, Void.class);
		assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Login
		UserLoginForm credentials = new UserLoginForm();
		credentials.setEmail("valid-login@mail.com");
		credentials.setPassword("foobar");
		HttpEntity<UserLoginForm> loginRequest = new HttpEntity<>(credentials, headers);

		ResponseEntity<Void> loginResponse = restTemplate.exchange("/auth", HttpMethod.POST, loginRequest, Void.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void wrongPasswordShouldFailAuthentication() {
		HttpHeaders headers = getJsonHeaders();

		// Register
		UserRegistrationForm user = new UserRegistrationForm();
		user.setFirstName("Foo");
		user.setLastName("Bar");
		user.setEmail("wrong-login@mail.com");
		user.setPassword("foobar");
		HttpEntity<UserRegistrationForm> registrationRequest = new HttpEntity<>(user, headers);

		ResponseEntity<Void> registrationResponse = restTemplate.exchange("/users", HttpMethod.POST, registrationRequest, Void.class);
		assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Login
		UserLoginForm credentials = new UserLoginForm();
		credentials.setEmail("wrong-login@mail.com");
		credentials.setPassword("foo");
		HttpEntity<UserLoginForm> loginRequest = new HttpEntity<>(credentials, headers);

		ResponseEntity<Void> loginResponse = restTemplate.exchange("/auth", HttpMethod.POST, loginRequest, Void.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void nonExistentUserShouldFailAuthentication() {
		HttpHeaders headers = getJsonHeaders();

		UserLoginForm credentials = new UserLoginForm();
		credentials.setEmail("nonexistent-login@mail.com");
		credentials.setPassword("foobar");
		HttpEntity<UserLoginForm> loginRequest = new HttpEntity<>(credentials, headers);

		ResponseEntity<Void> loginResponse = restTemplate.exchange("/auth", HttpMethod.POST, loginRequest, Void.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private HttpHeaders getJsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
