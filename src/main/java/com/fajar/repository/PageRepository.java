package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Page;

public interface PageRepository extends JpaRepository<Page		, Long>{

	Page findByCode(String code);

	List<Page> findByAuthorized(int i); 

}
