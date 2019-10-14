package com.fajar.dto;

import java.io.Serializable;

import com.fajar.annotation.Dto;
import com.fajar.entity.Customer;
import com.fajar.entity.Product;
import com.fajar.entity.Supplier;
import com.fajar.entity.Unit;
import com.fajar.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopApiRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110411933791444017L;

	private User user;
	private String entity;
	private Supplier supplier;
	private Customer customer;
	private Unit unit;
	private Product product;
	private Filter filter;

}
