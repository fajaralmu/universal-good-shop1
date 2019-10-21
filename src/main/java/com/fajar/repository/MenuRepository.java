package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Menu; 

public interface MenuRepository extends JpaRepository<Menu, Long> {

	List<Menu> findByPage(String string);

}
