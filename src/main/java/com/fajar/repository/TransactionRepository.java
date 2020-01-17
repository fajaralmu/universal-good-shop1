package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, RepositoryCustom<Transaction> {

	List<Transaction> findByType(String string);

	List<Transaction> findByTypeAndIdGreaterThan(String string, long l);

	List<Transaction> findByTypeAndIdGreaterThanAndIdLessThan(String string, long from, long to);

	@Query(nativeQuery = true, value = "select year( `transaction`.transaction_date) from `transaction` where "
			+ "`transaction`.transaction_date is not null  " + "order by transaction_date asc limit 1")
	Object findTransactionYearAsc();

	@Query(nativeQuery = true, value = "select * from `transaction` "
			+ "left join product_flow on product_flow.transaction_id = transaction.id "
			+ "where product_flow.product_id = ?1 and `transaction`.`type` = 'IN' "
			+ "group by supplier_id limit ?2 offset ?3")
	List<Transaction> findProductSupplier(Long id, int limit, int offset);

	@Query(nativeQuery = true, value = "select  * from `transaction` left join product_flow on `product_flow`.transaction_id=`transaction`.id  "
			+ "WHERE `product_flow`.product_id = ?1  and `transaction`.`type` = 'IN' "
			+ "order by `transaction`.transaction_date asc limit 1")
	List<Transaction> findFirstTransaction(Long productId);

}