package com.fajar.shoppingmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.Capital;

public interface CapitalRepository extends JpaRepository<Capital, Long> {

	List<Capital> findByDeletedFalse();

}
