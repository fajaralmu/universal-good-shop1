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
public class ProductFlowStock implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8491089980746895811L;
	private int totalStock;
	private int remainingStock;
	private int usedStock;
	private ProductFlow productFlow;
	
}
