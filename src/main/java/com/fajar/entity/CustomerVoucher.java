package com.fajar.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;

import lombok.Data;

@Data
@Entity
@Dto
@Table(name = "member_voucher")
public class CustomerVoucher extends BaseEntity{
	
	
	@JoinColumn(name = "voucher_id", nullable = true)
	@ManyToOne
	@FormField(entityReferenceName = "voucher", optionItemName = "name", type = FormField.FIELD_TYPE_DYNAMIC_LIST)
	private Voucher voucher;
	@Column(name="used_amount")
	@FormField(type=FormField.FIELD_TYPE_NUMBER)
	private long usedAmount;
	@JoinColumn(name = "customer_id", nullable = true)
	@ManyToOne
	@FormField(entityReferenceName = "customer", optionItemName = "name", type = FormField.FIELD_TYPE_DYNAMIC_LIST)
	private Customer member;
	

}
