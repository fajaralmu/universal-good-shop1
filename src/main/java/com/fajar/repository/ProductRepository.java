package com.fajar.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Product;

public interface ProductRepository extends JpaRepository<Product	, Long>{
	
}