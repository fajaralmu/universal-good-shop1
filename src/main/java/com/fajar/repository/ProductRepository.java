package com.fajar.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.entity.Product;

public interface ProductRepository extends JpaRepository<Product	, Long>{
	
	@Query(nativeQuery = true,value = "select * from product limit ?1 offset ?2")
	public List<Product> getByLimitAndOffset(int limit, int offset);
	
}