package com.xm.finbox.file;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.stereotype.Component;

@Component
class MimeTypeUtil {

	/**
	 * Simplistic mime type detection.
	 *
	 * see:
	 * https://dzone.com/articles/determining-file-types-java
	 * https://stackoverflow.com/questions/51438/getting-a-files-mime-type-in-java
	 *
	 * @param name the name of the file
	 * @param contents the contents of the file
	 *
	 * @return the suspected mime type
	 */
	String getMimeType(String name, byte[] contents) {
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		String mimeType = mimeTypesMap.getContentType(name);
		return mimeType;
	}

}
