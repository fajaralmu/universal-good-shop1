package com.fajar.shoppingmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer		, Long>{
	
}