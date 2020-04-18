package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.entity.CashBalance;

public interface CashBalanceRepository extends JpaRepository<CashBalance, Long> {

	public CashBalance findTop1ByOrderByIdDesc();

	@Query(nativeQuery = true, value = "select * from cash_balance where date < ?1 order by id desc limit 1")
	public CashBalance getCashAtMonthAndYear(String dateString);

}
