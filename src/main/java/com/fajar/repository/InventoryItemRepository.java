package com.fajar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, RepositoryCustom<InventoryItem>{

	InventoryItem  findByIncomingFlowId(Long flowReferenceId);

	List<InventoryItem> findByCountGreaterThan(int i);
 

}
