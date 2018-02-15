package com.xm.finbox.file;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FileCreateTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void validFileShouldBeCreated() {
		String authorization = FileUserUtil.user(restTemplate, "create-valid-file@mail.com");

		FileForm file = new FileForm();
		file.setName("create-valid-foo.txt");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<FileForm> createRequest = new HttpEntity<>(file, headers);

		ResponseEntity<FileMetadata> createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		FileMetadata metadata = createResponse.getBody();
		assertThat(metadata.getId()).isNotBlank();
		assertThat(metadata.getName()).isEqualTo("create-valid-foo.txt");
		assertThat(metadata.getMimeType()).isEqualTo("text/plain");
	}

	@Test
	public void invalidFileShouldFail() {
		String authorization = FileUserUtil.user(restTemplate, "create-invalid-file@mail.com");

		FileForm file = new FileForm();
		file.setName("");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<FileForm> createRequest = new HttpEntity<>(file, headers);

		ResponseEntity<FileMetadata> createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void duplicateFileShouldFail() {
		String authorization = FileUserUtil.user(restTemplate, "create-duplicate-file@mail.com");

		FileForm file = new FileForm();
		file.setName("create-duplicate-foo.txt");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<FileForm> createRequest = new HttpEntity<>(file, headers);

		ResponseEntity<FileMetadata> createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		FileMetadata metadata = createResponse.getBody();
		assertThat(metadata.getId()).isNotBlank();
		assertThat(metadata.getName()).isEqualTo("create-duplicate-foo.txt");
		assertThat(metadata.getMimeType()).isEqualTo("text/plain");


		file = new FileForm();
		file.setName("create-duplicate-foo.txt");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		createRequest = new HttpEntity<>(file, headers);

		createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}
