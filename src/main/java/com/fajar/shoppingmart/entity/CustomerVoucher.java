package com.fajar.shoppingmart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.FieldType;

import lombok.Data;

@Data
@Entity
@Dto
@Table(name = "member_voucher")
public class CustomerVoucher extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9107899093587915646L;
	@JoinColumn(name = "voucher_id", nullable = true)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Voucher voucher;
	@Column(name = "used_amount")
	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	private long usedAmount;
	@JoinColumn(name = "customer_id", nullable = true)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Customer member;

}
