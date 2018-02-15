package com.xm.finbox.file;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

@Service
@Transactional
public class FileService {

	@Autowired
	private FileRepository repository;

	@Autowired
	private FileMapper mapper;

	@Autowired
	private MimeTypeUtil mimeTypeUtil;

	public List<FileMetadata> list() {
		List<AppFile> files = repository.findAll();
		return mapper.mapList(files);
	}

	public FileMetadata create(String ownerEmail, FileForm fileDto) throws FileExistsException {
		boolean nameExists = repository.existsByName(fileDto.getName());
		if (nameExists) {
			throw new FileExistsException("File already exists");
		}

		AppFile file = new AppFile();
		String id = UUID.randomUUID().toString();
		file.setId(id);
		String name = fileDto.getName();
		file.setName(name);
		file.setCreationDate(new Date());
		file.setModificationDate(new Date());

		byte[] contents = Base64Utils.decodeFromString(fileDto.getContents());
		file.setContents(contents);
		file.setFileSize(Long.valueOf(contents.length));
		String mimeType = mimeTypeUtil.getMimeType(name, contents);
		file.setMimeType(mimeType);
		file.setOwnerEmail(ownerEmail);
		repository.save(file);

		return mapper.map(file);
	}

	public void delete(String ownerEmail, String fileId) throws FileNotFoundException, FileNotAllowedException {
		AppFile file = getFile(ownerEmail, fileId);
		repository.delete(file);
	}

	public FileMetadata getMetadata(String ownerEmail, String fileId) throws FileNotFoundException, FileNotAllowedException {
		AppFile file = getFile(ownerEmail, fileId);
		return mapper.map(file);
	}

	public byte[] getContents(String ownerEmail, String fileId) throws FileNotFoundException, FileNotAllowedException {
		AppFile file = getFile(ownerEmail, fileId);
		return file.getContents();
	}

	private AppFile getFile(String ownerEmail, String fileId) throws FileNotFoundException, FileNotAllowedException {
		Optional<AppFile> optionalFile = repository.findById(fileId);
		if (!optionalFile.isPresent()) {
			throw new FileNotFoundException(String.format("Cannot find file '%s'", fileId));
		}

		AppFile file = optionalFile.get();
		if (!file.getOwnerEmail().equals(ownerEmail)) {
			throw new FileNotAllowedException(String.format("Cannot access file '%s'", fileId));
		}

		return file;
	}
}
