package com.xm.finbox.file;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileMetadata {

	private String id;

	private String name;

	private Date creationDate;

	private Date modificationDate;

	private Long fileSize;

	private String mimeType;
}
