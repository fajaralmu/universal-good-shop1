package com.fajar.shoppingmart.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.entity.custom.BalanceJournalInfo;
import com.fajar.shoppingmart.entity.custom.FinancialEntity;
import com.fajar.shoppingmart.entity.custom.JournalInfoCost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(updateService = "costFlowUpdateService")
@Entity
@Table(name="cost_flow")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostFlow extends BaseEntity implements FinancialEntity{

	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6143899665323318955L;
	@Column 
	@FormField(type = FieldType.FIELD_TYPE_DATE)
	private Date date;
	@Column 
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description; 
	@Column 
	@FormField(type = FieldType.FIELD_TYPE_CURRENCY)
	private long nominal; 
	
	@JoinColumn(name="cost_id")
	@ManyToOne
	@FormField( type=FieldType.FIELD_TYPE_FIXED_LIST,optionItemName="name")
	private Cost costType;

	@Override
	public Date getTransactionDate() {
		 
		return date;
	}

	@Override
	public String getTransactionName() {
		 
		return description;
	}

	@Override
	public long getTransactionNominal() {
		return nominal;
	}

	@Override
	public BalanceJournalInfo<CostFlow> getBalanceJournalInfo() {
		return new JournalInfoCost(this); 
	}
  
	 
}
