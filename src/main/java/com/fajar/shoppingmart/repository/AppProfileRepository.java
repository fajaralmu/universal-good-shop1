package com.fajar.shoppingmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.Profile;

public interface AppProfileRepository extends JpaRepository<Profile, Long> {
 

	Profile findByAppCode(String appCode); 

}
