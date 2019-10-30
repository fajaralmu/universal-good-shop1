package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.ShopProfile;

public interface ShopProfileRepository extends JpaRepository<ShopProfile, Long>{

	ShopProfile findByMartCode(String martCode);

}
