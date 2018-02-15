package com.xm.finbox.security;

import java.util.Collections;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.xm.finbox.user.AppUser;
import com.xm.finbox.user.UserRepository;

@Service
@Transactional
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = userRepository.findByEmail(username);

		if (user == null) {
			throw new UsernameNotFoundException(String.format("Cannot find user '%s'.", username));
		}
		else {
			return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
		}
	}
}
