package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.dto.CashType;
import com.fajar.entity.CashBalance;

public interface CashBalanceRepository extends JpaRepository<CashBalance, Long> {

	public CashBalance findTop1ByOrderByIdDesc(); 

	/**
	 * 
	 * @param dateString 'yyyy-MM-dd' pattern
	 * @return Object[]
	 */
	@Query(nativeQuery = true, value = "select "
			+ " sum(credit_temp) as credit, sum(debit_temp) as debit, (sum(debit_temp) - sum(credit_temp)) as balance from cash_balance where "
			+ " date < ?1")
	public Object getBalanceBefore(String dateString);

	public CashBalance findTop1ByTypeAndReferenceId(CashType cashType, String valueOf);

}
