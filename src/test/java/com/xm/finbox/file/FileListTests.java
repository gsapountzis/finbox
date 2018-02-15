package com.xm.finbox.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FileListTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void validUserShouldListAllFiles() {
		String authorization = FileUserUtil.user(restTemplate, "create-list-files@mail.com");

		// Create first file
		FileForm file = new FileForm();
		file.setName("list-foo.txt");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<FileForm> createRequest = new HttpEntity<>(file, headers);

		ResponseEntity<FileMetadata> createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Create second file
		file = new FileForm();
		file.setName("list-bar.txt");
		file.setContents(Base64Utils.encodeToString("bar".getBytes()));

		headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		createRequest = new HttpEntity<>(file, headers);

		createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// List all files as different user
		authorization = FileUserUtil.user(restTemplate, "list-files@mail.com");

		headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<List<FileMetadata>> listRequest = new HttpEntity<>(headers);

		ResponseEntity<List<FileMetadata>> listResponse = restTemplate.exchange("/files", HttpMethod.GET, listRequest, new ParameterizedTypeReference<List<FileMetadata>>() {});
		assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<FileMetadata> metadata = listResponse.getBody();
		assertThat(metadata.size()).isEqualTo(2);
	}
}
