package com.fajar.shoppingmart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.dto.VoucherType;

import lombok.Data;

@Data
@Entity
@Dto
@Table(name="voucher")
public class Voucher extends BaseEntity {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8870003645847445058L;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private int month;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private int year;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_FIXED_LIST)
	private VoucherType type;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private long amount;
	@Column
	@FormField
	private String name;
}
