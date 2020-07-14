package com.fajar.shoppingmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.ShopProfile;

public interface AppProfileRepository extends JpaRepository<ShopProfile, Long> {

	ShopProfile findByMartCode(String martCode);

}
