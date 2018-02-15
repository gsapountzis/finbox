package com.xm.finbox.file;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<AppFile, String> {

	public AppFile findByName(String name);

	public boolean existsByName(String name);

}
