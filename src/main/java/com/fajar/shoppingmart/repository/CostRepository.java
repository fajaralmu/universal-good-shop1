package com.fajar.shoppingmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Cost;

public interface CostRepository extends JpaRepository<Cost		, Long>{

	List<Category> findByDeletedFalse();

}
