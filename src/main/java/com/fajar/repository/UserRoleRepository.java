package com.fajar.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.UserRole;

public interface UserRoleRepository extends JpaRepository< UserRole	, Long>{
	
}