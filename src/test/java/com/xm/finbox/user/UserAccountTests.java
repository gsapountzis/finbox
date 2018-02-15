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
public class UserAccountTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void authorizedUserShouldGetDetails() {
		HttpHeaders headers = getJsonHeaders();

		// Register
		UserRegistrationForm user = new UserRegistrationForm();
		user.setFirstName("Foo");
		user.setLastName("Bar");
		user.setEmail("valid-details@mail.com");
		user.setPassword("foobar");
		HttpEntity<UserRegistrationForm> registrationRequest = new HttpEntity<>(user, headers);

		ResponseEntity<Void> registrationResponse = restTemplate.exchange("/users", HttpMethod.POST, registrationRequest, Void.class);
		assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Login
		UserLoginForm credentials = new UserLoginForm();
		credentials.setEmail("valid-details@mail.com");
		credentials.setPassword("foobar");
		HttpEntity<UserLoginForm> loginRequest = new HttpEntity<>(credentials, headers);

		ResponseEntity<Void> loginResponse = restTemplate.exchange("/auth", HttpMethod.POST, loginRequest, Void.class);
		String authorization = loginResponse.getHeaders().get("Authorization").get(0);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(authorization).isNotBlank().startsWith("Bearer");

		// Get details
		headers.set("Authorization", authorization);
		HttpEntity<Void> detailsRequest = new HttpEntity<>(headers);

		ResponseEntity<Account> detailsResponse = restTemplate.exchange("/me", HttpMethod.GET, detailsRequest, Account.class);
		Account account = detailsResponse.getBody();
		assertThat(detailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(account.getFirstName()).isEqualTo("Foo");
		assertThat(account.getLastName()).isEqualTo("Bar");
		assertThat(account.getEmail()).isEqualTo("valid-details@mail.com");
	}

	@Test
	public void unauthorizedUserShouldNotGetDetails() {
		HttpHeaders headers = getJsonHeaders();
		headers.set("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI5NDM3MTU0OS05Mzg1LTQ2MWUtYmFhMi1iNGViYWM2MjU2MzMiLCJzdWIiOiJmb29AYmFyLmNvbSIsImlhdCI6MTUxODY1Mzg0Nn0.gwgrgln_31SjsHHBSb2z-YQR2xjMXUJbwgXVB6NVNuP7cxxmTpEe8wdfg0Knp4qt5a9cekGBx7EEWxBkE1udLA");
		HttpEntity<Void> detailsRequest = new HttpEntity<>(headers);

		ResponseEntity<Account> detailsResponse = restTemplate.exchange("/me", HttpMethod.GET, detailsRequest, Account.class);
		assertThat(detailsResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private HttpHeaders getJsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
