package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.CashBalance;

public interface CashBalanceRepository extends JpaRepository<CashBalance		, Long>{ 
	
	public CashBalance findTop1ByOrderByIdDesc();

}
