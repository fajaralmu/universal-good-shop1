package com.fajar.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.dto.CashType;
import com.fajar.dto.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name="cash_balance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashBalance extends BaseEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -1586384158220885834L;

	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	private long formerBalance;
	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	@Column(name="credit_temp")
	private long creditAmt;
	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	@Column(name="debit_temp")
	private long debitAmt ;
	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	private long actualBalance;
	@FormField
	@Enumerated(EnumType.STRING)
	@Column
	private CashType type;
	@FormField
	private Date date;
	@FormField
	private String referenceId;
	@FormField
	private String referenceInfo;
	
	
}
