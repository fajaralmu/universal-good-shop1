package com.fajar.shoppingmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.ProductFlow;

public interface ProductFlowRepository extends JpaRepository<ProductFlow, Long> {
	public List<ProductFlow> findByTransaction_Type(TransactionType type);

	public Optional<ProductFlow> findByIdAndTransaction_Type(Long id, TransactionType type);

	public List<ProductFlow> findByProduct_NameContainingAndTransactionType(String name, TransactionType type);

	public List<ProductFlow> findByPriceIsNull();

	public List<ProductFlow> findByTransaction_TypeAndTransaction_IdGreaterThan(String type, long l);

	public List<ProductFlow> findByTransaction_Id(Long id);

	@Query(nativeQuery = true, value = " select sum(product_flow.count) as used from product_flow "
			+ " left join product on product_flow.product_id = product.id "
			+ " left join `transaction` on transaction.id = product_flow.transaction_id "
			+ " where transaction.`type` = ?1 and product.id = ?2")
	public Object findProductFlowCount(String type, Long productId);

	@Query(nativeQuery = true, value = "select * from `product_flow` "
			+ "	 LEFT JOIN `transaction` ON  `transaction`.`id` = `product_flow`.`transaction_id`   "
			+ "	 WHERE  `transaction`.`type` = ?1 and month(`transaction`.transaction_date) = ?2  "
			+ "	 and year(`transaction`.transaction_date) = ?3 and `transaction`.deleted = false and `product_flow`.deleted = false")
	public List<ProductFlow> findByTransactionTypeAndPeriod(String type, int month, int year);

	@Query(nativeQuery = true, value = "select * from `product_flow`  "
			+ "	LEFT JOIN `transaction` ON  `transaction`.`id` = `product_flow`.`transaction_id`   "
			+ "	WHERE  `transaction`.`type` = ?1 and day(`transaction`.transaction_date) = ?2"
			+ " and month(`transaction`.transaction_date) = ?3 " + "	and year(`transaction`.transaction_date) = ?4 "
			+ " and `transaction`.deleted = false and `product_flow`.deleted = false")
	public List<ProductFlow> findByTransactionTypeAndPeriod(String type, int day, int month, int year);

	@Query(nativeQuery = true, value = "select * from `product_flow` "
			+ "	 LEFT JOIN `transaction` ON  `transaction`.`id` = `product_flow`.`transaction_id`   "
			+ "	 WHERE  month(`transaction`.transaction_date) = ?1  "
			+ "	 and year(`transaction`.transaction_date) = ?2 and `transaction`.deleted = false and `product_flow`.deleted = false")
	public List<ProductFlow> findByTransactionPeriod(int month, int year);

	@Query(nativeQuery = true, value = "select * from product_flow left join `transaction` on transaction_id = transaction.id "
			+ "where day(product_flow.created_date) != day(`transaction`.transaction_date) "
			+ "or month(product_flow.created_date) != month(`transaction`.transaction_date) "
			+ "or year(product_flow.created_date) != year(`transaction`.transaction_date)")
	public List<ProductFlow> FINDINCORRECTDATE(); 

	public List<ProductFlow> findByTransaction_TypeAndProduct_Id(TransactionType type, long productId);

	@Query(value = "select sum(count)  from product_flow left join `transaction` on transaction_id = transaction.id  "
			+ "where transaction.`type` = ?1 and product_id = ?2 ", nativeQuery = true)
	public int getCount(String trxType, long productId);

	
	@Query(value = "select sum(`product_flow`.count) as count, sum(`product_flow`.count * `product_flow`.price) as price,`transaction`.`type` as module from `product_flow`  " + 
			"  LEFT JOIN `transaction` ON  `transaction`.`id` = `product_flow`.`transaction_id` " + 
			"  WHERE `transaction`.`type` = ?1 and month(`transaction`.transaction_date) = ?2  " + 
			"  and year(`transaction`.transaction_date) = ?3 and `transaction`.deleted = false and `product_flow`.deleted = false "  , nativeQuery = true)
	public Object findCashflowByModuleAndMonthAndYear(TransactionType transactionType, Integer month, Integer year);
}