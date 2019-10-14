package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer		, Long>{
	
}