package com.fajar.shoppingmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.ProductFlow;

public interface ProductFlowRepository extends JpaRepository<ProductFlow, Long> {
	List<ProductFlow> findByTransaction_Type(TransactionType type);

	Optional<ProductFlow> findByIdAndTransaction_Type(Long id, TransactionType type);

	List<ProductFlow> findByProduct_NameContainingAndTransactionType(String name, TransactionType type);

	List<ProductFlow> findByPriceIsNull();

	List<ProductFlow> findByTransaction_TypeAndTransaction_IdGreaterThan(String type, long l);

	List<ProductFlow> findByTransaction_Id(Long id);

	@Query(nativeQuery = true, value = " select sum(product_flow.count) as used from product_flow "
			+ " left join product on product_flow.product_id = product.id "
			+ " left join `transaction` on transaction.id = product_flow.transaction_id "
			+ " where transaction.`type` = ?1 and product.id = ?2")
	Object findFlowCount(String type, Long productId);

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

//	String sql = "select * from product_flow left join `transaction` on transaction_id = transaction.id "
//	+ "left join product on product_id = product.id "
//	+ "where transaction.`type` = 'IN' and product.name like '%" + productName + "%' limit 20";

//String sql = "select product_flow.id as flowId, product_flow.count as flowCount, "
//	+ "(select sum(count) as total_count from product_flow where flow_ref_id=flowId and deleted!=1) as used,  "
//	+ " product_flow.* from product_flow  "
//	+ "left join `transaction` on product_flow.transaction_id = transaction.id "
//	+ "left join product on product_flow.product_id = product.id where transaction.`type` = 'IN' "
//	+ " and product." + key + " $CONDITION " + "having(used is null or flowCount-used>0) "
//	+ (limit > 0 ? " limit " + limit : "");

	List<ProductFlow> findByTransaction_TypeAndProduct_Id(TransactionType type, long productId);

	@Query(value = "select sum(count)  from product_flow left join `transaction` on transaction_id = transaction.id  "
			+ "where transaction.`type` = ?1 and product_id = ?2 ", nativeQuery = true)
	public int getCount(String trxType, long productId);
}