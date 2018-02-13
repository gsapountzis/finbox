package com.xm.finbox.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginForm {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

}
