package com.xm.finbox.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
class FileMapper {

	FileMetadata map(AppFile file) {
		FileMetadata metadata = new FileMetadata();
		metadata.setId(file.getId());
		metadata.setName(file.getName());
		metadata.setCreationDate(file.getCreationDate());
		metadata.setModificationDate(file.getModificationDate());
		metadata.setFileSize(file.getFileSize());
		metadata.setMimeType(file.getMimeType());
		return metadata;
	}

	List<FileMetadata> mapList(List<AppFile> files) {
		List<FileMetadata> metadata = new ArrayList<>();
		for (AppFile file : files) {
			metadata.add(map(file));
		}
		return metadata;
	}

}
