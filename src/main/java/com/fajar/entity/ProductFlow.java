package com.fajar.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.dto.FieldType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Dto
@Entity
@Table(name = "product_flow")
@Data
@Builder
@AllArgsConstructor
//@NoArgsConstructor
public class ProductFlow extends BaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 8994131360292840583L;
	@JoinColumn(name = "transaction_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "code", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Transaction transaction;
	@Column(name = "expiry_date")
	@FormField(type = FieldType.FIELD_TYPE_DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expiryDate;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_CURRENCY)
	private long price;
	@Column
	@FormField
	private int count;
	@Column(name = "flow_ref_id")
	@FormField
	private Long flowReferenceId;
	@JoinColumn(name = "product_id")
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Product product;
	@Transient
	private long transactionId;
	@Transient
	private ProductFlowStock productFlowStock;
	@FormField(multiply = { "count", "price" })
	@Transient
	private long totalPrice;

	public ProductFlow() {
		// System.out.println("---------------CALL THIS:"+this);
	}

}
