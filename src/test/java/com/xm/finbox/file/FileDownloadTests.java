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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FileDownloadTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void userShouldDownloadFile() {
		String authorization = FileUserUtil.user(restTemplate, "download-valid-file@mail.com");

		// Create file
		FileForm file = new FileForm();
		file.setName("download-valid-foo.txt");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<FileForm> createRequest = new HttpEntity<>(file, headers);

		ResponseEntity<FileMetadata> createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		FileMetadata metadata = createResponse.getBody();
		String fileId = metadata.getId();
		assertThat(fileId).isNotBlank();

		// Download file
		headers = new HttpHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<byte[]> downloadRequest = new HttpEntity<>(headers);

		ResponseEntity<byte[]> downloadResponse = restTemplate.exchange("/files/{fileId}/contents", HttpMethod.GET, downloadRequest, byte[].class, fileId);
		assertThat(downloadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		byte[] content = downloadResponse.getBody();
		assertThat(content).isEqualTo("foo".getBytes());

		headers = downloadResponse.getHeaders();
		assertThat(headers.getContentType()).isEqualTo(MediaType.valueOf("text/plain"));
		assertThat(headers.getContentLength()).isEqualTo(3);
	}

	@Test
	public void userShouldFailDownloadNonExistentFile() {
		String authorization = FileUserUtil.user(restTemplate, "download-non-existent-file@mail.com");

		// Dowload non-existent file
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<Void> downloadRequest = new HttpEntity<>(headers);

		String fileId = UUID.randomUUID().toString();
		ResponseEntity<byte[]> downloadResponse = restTemplate.exchange("/files/{fileId}/contents", HttpMethod.GET, downloadRequest, byte[].class, fileId);
		assertThat(downloadResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void unauthorizedUserShouldFailDownloadFile() {
		String authorization = FileUserUtil.user(restTemplate, "create-download-file@mail.com");

		// Create file
		FileForm file = new FileForm();
		file.setName("create-download-foo.txt");
		file.setContents(Base64Utils.encodeToString("foo".getBytes()));

		HttpHeaders headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<FileForm> createRequest = new HttpEntity<>(file, headers);

		ResponseEntity<FileMetadata> createResponse = restTemplate.exchange("/files", HttpMethod.POST, createRequest, FileMetadata.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		FileMetadata metadata = createResponse.getBody();
		String fileId = metadata.getId();
		assertThat(fileId).isNotBlank();

		// Download file
		authorization = FileUserUtil.user(restTemplate, "download-unauth-file@mail.com");

		headers = FileUserUtil.getJsonHeaders();
		headers.set("Authorization", authorization);
		HttpEntity<Void> downloadRequest = new HttpEntity<>(headers);

		ResponseEntity<byte[]> downloadResponse = restTemplate.exchange("/files/{fileId}/contents", HttpMethod.GET, downloadRequest, byte[].class, fileId);
		assertThat(downloadResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}
