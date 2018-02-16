package com.xm.finbox.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private UserMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public String register(UserRegistrationForm userDto) throws UserExistsException {
		boolean emailExists = repository.existsByEmail(userDto.getEmail());
		if (emailExists) {
			throw new UserExistsException("User already exists");
		}

		AppUser user = mapper.map(userDto);
		String id = UUID.randomUUID().toString();
		user.setId(id);
		String password = passwordEncoder.encode(userDto.getPassword());
		user.setPassword(password);
		repository.save(user);
		return id;
	}

	public Account getAccoutDetails(String email) {
		AppUser user = repository.findByEmail(email);
		if (user == null) {
			return null;
		}

		Account account = mapper.map(user);
		return account;
	}

}
