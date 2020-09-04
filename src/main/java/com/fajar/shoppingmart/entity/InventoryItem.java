package com.fajar.shoppingmart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.shoppingmart.annotation.Dto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name = "inventoryitem")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8288927570426154116L;
	@Column(name = "incoming_flow_id", nullable = false)
	private long incomingFlowId;
	@Column(nullable = false)
	private int count;
	@JoinColumn(name = "product_id", nullable = false)
	@ManyToOne
	private Product product;
	@Column(name = "original_count", nullable = false)
	private int originalCount;
	/**
	 * the newVersion will not determine poductFLow
	 */
	@Column(name = "new_version")
	private boolean newVersion;

	@Transient
	@JsonIgnore
	private ProductFlow productFlow;

	public InventoryItem(Product product2, int count2) { 
		setProduct(product2);
		setCount(count2);
	}
	
	public InventoryItem(ProductFlow productFlow) {
		this.productFlow = productFlow;
	}

	public void takeProduct(int count) {
		this.count = this.count - count;
	}

	public void addProduct(int count) {
		this.count = this.count + count;
	}

	public void addNewProduct() {
		setProduct(productFlow.getProduct());
		setCount(productFlow.getCount());
		setOriginalCount(productFlow.getCount());
		setId(productFlow.getId());
		setIncomingFlowId(productFlow.getId());
	}

	/**
	 * check if inventories is enough for this selling
	 * 
	 * @param _productFlow2
	 * @return
	 */
	public boolean hasEnoughStock(ProductFlow _productFlow2) {

		return getCount() - _productFlow2.getCount() >= 0;
	}

	public static InventoryItem createAndAddNewProduct(ProductFlow productFlow2) {
		InventoryItem inventoryItem = new InventoryItem(productFlow2);
		inventoryItem.addNewProduct();
		return inventoryItem;
	}

	

}
