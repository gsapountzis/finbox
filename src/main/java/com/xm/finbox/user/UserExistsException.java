package com.xm.finbox.user;

public class UserExistsException extends RuntimeException {

	public UserExistsException(String message) {
		super(message);
	}

}
