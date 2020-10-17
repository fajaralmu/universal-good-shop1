package com.fajar.shoppingmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	public List<Transaction> findByType(TransactionType type);

	public List<Transaction> findByTypeAndIdGreaterThan(TransactionType type, long l);

	public List<Transaction> findByTypeAndIdGreaterThanAndIdLessThan(TransactionType type, long from, long to);

	@Query(nativeQuery = true, value = "select year( `transaction`.transaction_date) from `transaction` where "
			+ "`transaction`.transaction_date is not null  " + "order by transaction_date asc limit 1")
	public Object findTransactionYearAsc();

	@Query(nativeQuery = true, value = "select * from `transaction` "
			+ "left join product_flow on product_flow.transaction_id = transaction.id "
			+ "where product_flow.product_id = ?1 and `transaction`.`type` = 'PURCHASING' "
			+ "group by supplier_id limit ?2 offset ?3")
	public List<Transaction> findProductSupplier(Long id, int limit, int offset);

	@Query(nativeQuery = true, value = "select  * from `transaction` left join product_flow on `product_flow`.transaction_id=`transaction`.id  "
			+ "WHERE `product_flow`.product_id = ?1  and `transaction`.`type` = 'PURCHASING' "
			+ "order by `transaction`.transaction_date asc limit 1")
	public List<Transaction> findFirstTransaction(Long productId);

	@Query(nativeQuery = true, value = "select * from `transaction`where type=?1"
			+ " and year(`transaction_date`) = ?3 and month(`transaction_date`) = ?2 ")
	public List<Transaction> findTransactionByTypeAndPeriod(String type, int month, int year);

}