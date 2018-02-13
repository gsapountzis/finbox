package com.xm.finbox.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

	public boolean existsByEmail(String email);

}
