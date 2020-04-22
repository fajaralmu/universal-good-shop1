package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Capital;

public interface CapitalRepository extends JpaRepository<Capital		, Long>{

	List<Capital> findByDeletedFalse();

}