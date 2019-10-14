package com.fajar.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiRequest.ShopApiRequestBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name="product_flow")@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFlow extends BaseEntity implements Serializable{

	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 8994131360292840583L;
	@JoinColumn(name="transaction_id")
	@ManyToOne
	private Transaction transaction;
	@Column(name="expiry_date")
	private Date expiryDate;
	@Column
	private Long price;
	@Column
	private Integer count;
	@Column(name="flow_ref_id")
	private Long flowReferenceId;
	@JoinColumn(name="product_id")
	@ManyToOne
	private Product product;
	
}
