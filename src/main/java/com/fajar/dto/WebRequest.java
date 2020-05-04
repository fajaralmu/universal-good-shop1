package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fajar.annotation.Dto;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Capital;
import com.fajar.entity.CapitalFlow;
import com.fajar.entity.Category;
import com.fajar.entity.Cost;
import com.fajar.entity.CostFlow;
import com.fajar.entity.Customer;
import com.fajar.entity.CustomerVoucher;
import com.fajar.entity.Menu;
import com.fajar.entity.Page;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.RegisteredRequest;
import com.fajar.entity.ShopProfile;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.Unit;
import com.fajar.entity.User;
import com.fajar.entity.UserRole;
import com.fajar.entity.Voucher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110411933791444017L;


	
	/**
	 * ENTITY CRUD use lowerCase!!!
	 */

	private String entity;
	private User user;
	private Supplier supplier;
	private Customer customer;
	private ShopProfile shopprofile;
	private Menu menu;
	private Unit unit;
	private Product product;
	private UserRole userrole;
	private Category category;
	private RegisteredRequest registeredRequest; 
	private Cost cost;
	private CostFlow costflow;
	private Voucher voucher;
	private CustomerVoucher customervoucher;
	private Capital capital;
	private CapitalFlow capitalflow;
	private Page page;

	/**
	 * ==========end entity============
	 */

	private Filter filter;
	private ProductFlow productFlow;
	private Transaction transaction;
	
	private BaseEntity entityObject;
	
	private String destination;
	private String username;
	private String value;
	
	@Builder.Default
	private List<ProductFlow> productFlows = new ArrayList<ProductFlow>();

}
