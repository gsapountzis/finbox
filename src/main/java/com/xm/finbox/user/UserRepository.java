package com.xm.finbox.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, String> {

	public AppUser findByEmail(String email);

	public boolean existsByEmail(String email);

}
