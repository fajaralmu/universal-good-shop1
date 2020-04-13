package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.CashBalance;
import com.fajar.entity.Category;
import com.fajar.entity.Cost;

public interface CashBalanceRepository extends JpaRepository<CashBalance		, Long>{

	List<Category> findByDeletedFalse();

}
