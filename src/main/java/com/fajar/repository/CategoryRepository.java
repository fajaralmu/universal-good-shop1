package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Category;

public interface CategoryRepository extends JpaRepository<Category		, Long>{

	List<Category> findByDeletedFalse();

}
