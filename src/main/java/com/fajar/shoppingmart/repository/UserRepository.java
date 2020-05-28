package com.fajar.shoppingmart.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.User;

public interface UserRepository extends JpaRepository< User	, Long>{

	User findByUsernameAndPassword(String username, String password);

	User findByUsername(String username);
	
}