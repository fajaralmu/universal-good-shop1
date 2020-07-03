package com.fajar.shoppingmart.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.CashType;
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.service.entity.BaseEntityUpdateService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(updateService = BaseEntityUpdateService.class)
@Entity
@Table(name="cash_balance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashBalance extends BaseEntity {/**
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
