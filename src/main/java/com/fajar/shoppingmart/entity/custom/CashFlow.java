package com.fajar.shoppingmart.entity.custom;
 
import com.fajar.shoppingmart.annotation.CustomEntity;
import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CustomEntity(propOrder = {"count", "amount", "module"})
public class CashFlow extends BaseEntity{

	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 4767704206272308090L;
	private long amount;
	private long count;
	private String module;
	private int year;
	private int month;
	private Product product;
	
	private double proportion;
	
}
