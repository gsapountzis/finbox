package com.xm.finbox.user;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private UserMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public String register(UserRegisterForm userDto) throws UserExistsException {
		boolean emailExists = repository.existsByEmail(userDto.getEmail());
		if (emailExists) {
			throw new UserExistsException("User already exists");
		}

		User user = mapper.map(userDto);
		String id = UUID.randomUUID().toString();
		user.setId(id);
		String encodedPassword = passwordEncoder.encode(userDto.getPassword());
		user.setEncodedPassword(encodedPassword);
		repository.save(user);
		return id;
	}

}
