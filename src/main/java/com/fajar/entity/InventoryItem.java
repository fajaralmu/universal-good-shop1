package com.fajar.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.annotation.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto
@Entity
@Table(name="inventoryitem")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8288927570426154116L;
	@Column(name="incoming_flow_id",nullable = false)
	private long incomingFlowId;
	@Column(nullable = false)
	private int count;
	@JoinColumn(name="product_id",nullable = false)
	@ManyToOne
	private Product product;
	@Column(name="original_count",nullable = false)
	private int originalCount;
	/**
	 * the newVersion will not determine poductFLow
	 */
	@Column(name="new_version")
	private boolean newVersion;

}
