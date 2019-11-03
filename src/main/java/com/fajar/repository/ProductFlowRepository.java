package com.fajar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.ProductFlow;

public interface ProductFlowRepository extends JpaRepository<ProductFlow, Long> {
	List<ProductFlow> findByTransaction_Type(String type);

	Optional<ProductFlow> findByIdAndTransaction_Type(Long id, String type);

	List<ProductFlow> findByProduct_NameContainingAndTransactionType(String name, String string);

	List<ProductFlow> findByPriceIsNull();

	List<ProductFlow> findByTransaction_TypeAndTransaction_IdGreaterThan(String string, long l);
}