package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, RepositoryCustom<Transaction> {

	List<Transaction> findByType(String string);

	List<Transaction> findByTypeAndIdGreaterThan(String string, long l);

	List<Transaction> findByTypeAndIdGreaterThanAndIdLessThan(String string, long from, long to);

}