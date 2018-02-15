package com.xm.finbox.file;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

	@Autowired
	private FileService fileService;

	/**
	 * This endpoint is used to retrieve all user’s files.
	 *
	 * @return
	 * <ul>
	 * <li>Returns 200 if list is retrieved</li>
	 * <li>Returns 401 if user not authenticated</li>
	 * <li>Returns 500 for any other error</li>
	 * </ul>
	 */
	@GetMapping(path = "/files", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FileMetadata>> list() {
		List<FileMetadata> metadata = fileService.list();
		return ResponseEntity.ok(metadata);
	}

	/**
	 * This endpoint is used to create a new file.
	 *
	 * The filename in the database must be the filename of the file just uploaded.
	 *
	 * XXX Questions for file create:
	 * is file name unique globally or per-user ?
	 * if create only creates, I should return 201 ?
	 * if create only creates, when is a file modified ?
	 * missing status code for file exists case, use conflict 409 ?
	 *
	 * @param file the name and contents of the new file
	 * @return
	 * <ul>
	 * <li>Returns 200 if file is retrieved (created ? 201 ?)</li>
	 * <li>Returns 401 if user not authenticated</li>
	 * <li>Returns 500 for any other error</li>
	 * </ul>
	 */
	@PostMapping(path = "/files", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FileMetadata> create(Authentication auth, @Valid @RequestBody FileForm file) {
		try {
			FileMetadata metadata = fileService.create(auth.getName(), file);
			return ResponseEntity.ok(metadata);
		}  catch (FileExistsException ex) {
			// TODO set error details document
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * This endpoint is used to delete a file which belongs to the logged in user.
	 *
	 * XXX Questions for file delete:
	 * 404 seems more appropriate for file does not exist / does not belong to user
	 *
	 * @param fileId the id of the file to be deleted
	 *
	 * @return
	 * <ul>
	 * <li>Returns 204 if file was deleted</li>
	 * <li>Returns 400 if validation error</li>
	 * <li>Returns 401 if user not authenticated</li>
	 * <li>Returns 400 if file does not belong to user</li>
	 * <li>Returns 500 for any other error</li>
	 * </ul>
	 */
	@DeleteMapping(path = "/files/{fileId}")
	public ResponseEntity<Void> delete(Authentication auth, @PathVariable String fileId) {
		try {
			fileService.delete(auth.getName(), fileId);
			return ResponseEntity.noContent().build();
		}  catch (FileNotFoundException ex) {
			// TODO set error details document
			return ResponseEntity.badRequest().build();
		}  catch (FileNotAllowedException ex) {
			// TODO set error details document
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * This endpoint is used to download the contents of a file.
	 *
	 * XXX Questions for file download:
	 * 404 seems more appropriate for file does not exist / does not belong to user
	 *
	 * @param fileId the id of the file to be deleted
	 *
	 * @return
	 * <ul>
	 * <li>Returns 200 if file contents are returned</li>
	 * <li>Returns 400 if validation error</li>
	 * <li>Returns 401 if user not authenticated</li>
	 * <li>Returns 400 if file does not belong to user</li>
	 * <li>Returns 500 for any other error</li>
	 * </ul>
	 */
	@GetMapping(path = "/files/{fileId}/contents")
	public ResponseEntity<byte[]> download(Authentication auth, @PathVariable String fileId) {
		try {
			// TODO implement streaming download
			FileMetadata metadata = fileService.getMetadata(auth.getName(), fileId);
			byte[] contents = fileService.getContents(auth.getName(), fileId);

			// Create Content-Disposition header
			ContentDisposition contentDisposition = ContentDisposition.builder("inline")
					.name(metadata.getName())
					.filename(metadata.getName())
					.size(metadata.getFileSize())
					.creationDate(zoned(metadata.getCreationDate()))
					.modificationDate(zoned(metadata.getModificationDate()))
					.build();

			// Set Content related headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(contentDisposition);
			headers.setContentType(MediaType.valueOf(metadata.getMimeType()));
			headers.setContentLength(metadata.getFileSize());
			headers.setLastModified(metadata.getModificationDate().getTime());

			return ResponseEntity.ok().headers(headers).body(contents);
		}  catch (FileNotFoundException ex) {
			// TODO set error details document
			return ResponseEntity.badRequest().build();
		}  catch (FileNotAllowedException ex) {
			// TODO set error details document
			return ResponseEntity.badRequest().build();
		}
	}

	private static ZonedDateTime zoned(Date date) {
		return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
}
