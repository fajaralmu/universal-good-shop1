package com.fajar.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Transaction;

public interface TransactionRepository extends JpaRepository< Transaction	, Long>{
	
}