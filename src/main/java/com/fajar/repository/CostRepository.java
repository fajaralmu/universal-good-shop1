package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Category;
import com.fajar.entity.Cost;

public interface CostRepository extends JpaRepository<Cost		, Long>{

	List<Category> findByDeletedFalse();

}
