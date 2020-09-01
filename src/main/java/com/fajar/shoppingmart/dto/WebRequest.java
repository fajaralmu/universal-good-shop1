package com.fajar.shoppingmart.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Capital;
import com.fajar.shoppingmart.entity.CapitalFlow;
import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Cost;
import com.fajar.shoppingmart.entity.CostFlow;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.CustomerVoucher;
import com.fajar.shoppingmart.entity.Menu;
import com.fajar.shoppingmart.entity.Page;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.RegisteredRequest;
import com.fajar.shoppingmart.entity.Profile;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.Unit;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.UserRole;
import com.fajar.shoppingmart.entity.Voucher;

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
	private Profile profile;
	private Menu menu;
	private Unit unit;
	private Product product;
	private UserRole userrole;
	private Category category;
	private RegisteredRequest registeredrequest; 
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
	
	private List<BaseEntity> orderedEntities;
	
	@Builder.Default
	private List<ProductFlow> productFlows = new ArrayList<ProductFlow>();
	
	private String imageData;
	private String partnerId;
	private String originId;

}
