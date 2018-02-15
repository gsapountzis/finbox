package com.xm.finbox.user;

import org.springframework.stereotype.Component;

@Component
class UserMapper {

	AppUser map(UserRegistrationForm userDto) {
		AppUser user = new AppUser();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		return user;
	}

	Account map(AppUser user) {
		Account account = new Account();
		account.setFirstName(user.getFirstName());
		account.setLastName(user.getLastName());
		account.setEmail(user.getEmail());
		return account;
	}

}
