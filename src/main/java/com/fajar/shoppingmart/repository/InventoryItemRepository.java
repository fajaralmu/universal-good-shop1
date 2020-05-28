package com.fajar.shoppingmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, RepositoryCustom<InventoryItem>{

	InventoryItem  findByIncomingFlowId(Long flowReferenceId);

	List<InventoryItem> findByCountGreaterThan(int i);

	InventoryItem findTop1ByProduct_IdAndNewVersion(Long id, boolean newVersion);
 

}
