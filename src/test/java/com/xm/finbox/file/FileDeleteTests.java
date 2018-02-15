package com.xm.finbox.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

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
public class FileDeleteTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void userShouldDeleteFile() {
		String authorization = FileUserUtil.user(restTemplate, "delete-valid-file@mail.com");

		// Create file
		FileForm file = new FileForm();
		file.setName("delete-valid-foo.txt");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<FileForm> createRequest = new HttpEntity<>(file, headers);

		ResponseEntity<FileMetadata> createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		FileMetadata metadata = createResponse.getBody();
		String fileId = metadata.getId();
		assertThat(fileId).isNotBlank();

		// Delete file
		headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate.exchange("/files/{fileId}", HttpMethod.DELETE, deleteRequest, Void.class, fileId);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void userShouldFailDeleteNonExistentFile() {
		String authorization = FileUserUtil.user(restTemplate, "delete-non-existent-file@mail.com");

		// Delete non-existent file
		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);

		String fileId = UUID.randomUUID().toString();
		ResponseEntity<Void> deleteResponse = restTemplate.exchange("/files/{fileId}", HttpMethod.DELETE, deleteRequest, Void.class, fileId);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void unauthorizedUserShouldFailDeleteFile() {
		String authorization = FileUserUtil.user(restTemplate, "create-delete-file@mail.com");

		// Create file
		FileForm file = new FileForm();
		file.setName("create-delete-foo.txt");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<FileForm> createRequest = new HttpEntity<>(file, headers);

		ResponseEntity<FileMetadata> createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		FileMetadata metadata = createResponse.getBody();
		String fileId = metadata.getId();
		assertThat(fileId).isNotBlank();

		// Delete file
		authorization = FileUserUtil.user(restTemplate, "delete-nice-file@mail.com");

		headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate.exchange("/files/{fileId}", HttpMethod.DELETE, deleteRequest, Void.class, fileId);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}
