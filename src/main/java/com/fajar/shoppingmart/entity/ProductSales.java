package com.fajar.shoppingmart.entity;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.util.DateUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSales extends BaseEntity {
	
	/**
	* 
	*/
	private static final long serialVersionUID = -1169438325218194178L;
	private Product product;
	private int sales;
	@Setter(value = AccessLevel.NONE)
	private int month;
	private int year;
	private double percentage; 
	private int maxValue;
	private String monthName;
	
	public ProductSales(int month2, int year2) {
		setMonth(month2);
		this.year = year2;
	}

	public void setMonth(int month2) {
		 
		this.month = month2;
		this.monthName = DateUtil.MONTH_NAMES[month-2];
	}

}
