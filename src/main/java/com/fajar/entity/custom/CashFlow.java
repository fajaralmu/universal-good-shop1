package com.fajar.entity.custom;
 
import java.io.Serializable;

import com.fajar.annotation.CustomEntity;
import com.fajar.annotation.Dto;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Product;

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
public class CashFlow extends BaseEntity implements Serializable{

	 
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
	
}
