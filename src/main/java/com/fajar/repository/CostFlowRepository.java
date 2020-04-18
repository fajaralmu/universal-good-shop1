package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.entity.CostFlow;

public interface CostFlowRepository extends JpaRepository<CostFlow, Long> {

	@Query(nativeQuery = true, value = "select * from cost_flow where month(`date`) = ?1 and year(`date`) = ?2")
	List<CostFlow> findByPeriod(int month, int year);

}
