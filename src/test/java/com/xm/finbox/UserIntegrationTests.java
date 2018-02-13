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

import com.xm.finbox.user.UserRegisterForm;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserIntegrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void validUserShouldRegister() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		UserRegisterForm user = new UserRegisterForm();
		user.setFirstName("Valid");
		user.setLastName("User");
		user.setEmail("valid@gmail.com");
		user.setPassword("foobar");
		HttpEntity<UserRegisterForm> request = new HttpEntity<UserRegisterForm>(user, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/users", HttpMethod.POST, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getLocation().toString()).endsWith(this.port + "/auth");
	}

	@Test
	public void invalidUserShouldNotRegister() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		UserRegisterForm user = new UserRegisterForm();
		user.setFirstName("Invalid");
		user.setLastName("Password");
		user.setEmail("invalid@gmail.com");
		user.setPassword("foo");
		HttpEntity<UserRegisterForm> request = new HttpEntity<UserRegisterForm>(user, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/users", HttpMethod.POST, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void duplicateUserShouldNotRegister() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		UserRegisterForm user = new UserRegisterForm();
		user.setFirstName("First");
		user.setLastName("User");
		user.setEmail("user@gmail.com");
		user.setPassword("foobar");
		HttpEntity<UserRegisterForm> request = new HttpEntity<UserRegisterForm>(user, headers);

		ResponseEntity<Void> response = restTemplate.exchange("/users", HttpMethod.POST, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		UserRegisterForm duplicateUser = new UserRegisterForm();
		duplicateUser.setFirstName("Duplicate");
		duplicateUser.setLastName("User");
		duplicateUser.setEmail("user@gmail.com");
		duplicateUser.setPassword("foobar");
		HttpEntity<UserRegisterForm> duplicateRequest = new HttpEntity<UserRegisterForm>(duplicateUser, headers);

		ResponseEntity<Void> duplicateResponse = restTemplate.exchange("/users", HttpMethod.POST, duplicateRequest, Void.class);
		assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

}
