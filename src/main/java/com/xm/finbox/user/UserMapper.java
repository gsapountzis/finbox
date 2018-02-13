package com.xm.finbox.user;

import org.springframework.stereotype.Component;

@Component
class UserMapper {

	User map(UserRegisterForm userDto) {
		User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		return user;
	}
}
