package com.fajar.shoppingmart.entity;

import java.io.Serializable;

import com.fajar.shoppingmart.annotation.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSales extends BaseEntity implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -1169438325218194178L;
	private Product product;
	private int sales;
	private int month;
	private int year;
	private double percentage;

}
