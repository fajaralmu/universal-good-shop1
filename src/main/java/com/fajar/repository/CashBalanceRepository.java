package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.entity.CashBalance;

public interface CashBalanceRepository extends JpaRepository<CashBalance, Long> {

	public CashBalance findTop1ByOrderByIdDesc();

	@Query(value = "select * from cash_balance where referenceId = ?2 and referenceInfo like %?1% ", nativeQuery = true)
	public CashBalance getByReferenceInfoAndReferenceId(String reffInfo, String reffId);

//	@Query(nativeQuery = true, value = "select * from cash_balance where month(date) = ?1 and year(date) = ?2 order by id  desc limit 1")
//	public CashBalance getCashBalanceAt(int month, int year);

	/**
	 * 
	 * @param dateString 'yyyy-MM-dd' pattern
	 * @return
	 */
	@Query(nativeQuery = true, value = "select "
			+ " sum(creditAmount) as credit, sum(debitAmount) as debit, (sum(creditAmount) - sum(debitAmount)) as balance from cash_balance where  \r\n"
			+ " date < ?1")
	public Object getBalanceBefore(String dateString);

}
