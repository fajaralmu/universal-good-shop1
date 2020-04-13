package com.fajar.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.dto.VoucherType;

import lombok.Data;

@Data
@Entity
@Dto
@Table(name="voucher")
public class Voucher extends BaseEntity implements Serializable{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8870003645847445058L;
	@Column
	@FormField(type=FormField.FIELD_TYPE_NUMBER)
	private int month;
	@Column
	@FormField(type=FormField.FIELD_TYPE_NUMBER)
	private int year;
	@Column
	@FormField(type=FormField.FIELD_TYPE_FIXED_LIST, defaultValues = {
			"PRIMARY","SERVICE"
	} )
	private VoucherType type;
	@Column
	@FormField(type=FormField.FIELD_TYPE_NUMBER)
	private long amount;
	@Column
	@FormField
	private String name;
}
