package com.xm.finbox;

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

import com.xm.finbox.user.UserRegistrationForm;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserRegistrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void validUserShouldRegister() {
		HttpHeaders headers = getJsonHeaders();

		UserRegistrationForm user = new UserRegistrationForm();
		user.setFirstName("Foo");
		user.setLastName("Bar");
		user.setEmail("valid-registration@mail.com");
		user.setPassword("foobar");
		HttpEntity<UserRegistrationForm> request = new HttpEntity<UserRegistrationForm>(user, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/users", HttpMethod.POST, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getLocation().toString()).endsWith(this.port + "/auth");
	}

	@Test
	public void invalidUserShouldNotRegister() {
		HttpHeaders headers = getJsonHeaders();

		UserRegistrationForm user = new UserRegistrationForm();
		user.setFirstName("Foo");
		user.setLastName("Bar");
		user.setEmail("invalid-registration@mail.com");
		user.setPassword("foo");
		HttpEntity<UserRegistrationForm> request = new HttpEntity<UserRegistrationForm>(user, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/users", HttpMethod.POST, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void duplicateUserShouldNotRegister() {
		HttpHeaders headers = getJsonHeaders();

		UserRegistrationForm user = new UserRegistrationForm();
		user.setFirstName("Foo");
		user.setLastName("Bar");
		user.setEmail("registration@mail.com");
		user.setPassword("foobar");
		HttpEntity<UserRegistrationForm> request = new HttpEntity<UserRegistrationForm>(user, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/users", HttpMethod.POST, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		UserRegistrationForm duplicateUser = new UserRegistrationForm();
		duplicateUser.setFirstName("Foo");
		duplicateUser.setLastName("Baz");
		duplicateUser.setEmail("registration@mail.com");
		duplicateUser.setPassword("foobar");
		HttpEntity<UserRegistrationForm> duplicateRequest = new HttpEntity<UserRegistrationForm>(duplicateUser, headers);

		ResponseEntity<Void> duplicateResponse = restTemplate.exchange("/users", HttpMethod.POST, duplicateRequest, Void.class);
		assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	private HttpHeaders getJsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
