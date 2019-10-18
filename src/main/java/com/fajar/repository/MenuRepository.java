package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Menu; 

public interface MenuRepository extends JpaRepository<Menu, Long> {

}
