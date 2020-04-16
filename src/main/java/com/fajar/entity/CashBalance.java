package com.fajar.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.dto.CashType;

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

	@FormField(type = FormField.FIELD_TYPE_NUMBER)
	private long formerBalance;
	@FormField(type = FormField.FIELD_TYPE_NUMBER)
	private long creditAmount;
	@FormField(type = FormField.FIELD_TYPE_NUMBER)
	private long debitAmount;
	@FormField(type = FormField.FIELD_TYPE_NUMBER)
	private long actualBalance;
	@FormField
	private CashType type;
	@FormField
	private Date date;
	@FormField
	private String referenceId;
	@FormField
	private String referenceInfo;
	
	
}
