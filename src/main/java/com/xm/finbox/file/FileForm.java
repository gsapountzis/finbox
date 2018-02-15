package com.xm.finbox.file;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileForm {

	@NotBlank
	private String name;

	@NotBlank
	private String contents;
}
