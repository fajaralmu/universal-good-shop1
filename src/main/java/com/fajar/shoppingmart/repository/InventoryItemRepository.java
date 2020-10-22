package com.fajar.shoppingmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.shoppingmart.entity.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

	public InventoryItem  findByIncomingFlowId(Long flowReferenceId);

	public List<InventoryItem> findByCountGreaterThan(int i);

	public InventoryItem findTop1ByProduct_IdAndNewVersion(Long id, boolean newVersion);
 
	@Query(value="select sum(count) from inventoryitem where incoming_flow_id = 0 ", nativeQuery = true)
	public Integer getAllInventoriesStock();

}
