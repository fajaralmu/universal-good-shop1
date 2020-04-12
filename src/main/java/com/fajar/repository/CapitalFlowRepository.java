package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.Capital;
import com.fajar.entity.CapitalFlow;

public interface CapitalFlowRepository extends JpaRepository<CapitalFlow		, Long>{

	List<CapitalFlow> findByDeletedFalse();

}
