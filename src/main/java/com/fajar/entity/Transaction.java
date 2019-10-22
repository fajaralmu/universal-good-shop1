package com.fajar.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiRequest.ShopApiRequestBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = -5995699726278991966L;

	
	@JoinColumn(name = "customer_id")
	@ManyToOne
	@FormField (entityReferenceName="customer",optionItemName="name",type="dynamiclist")
	private Customer customer;
	@JoinColumn(name = "supplier_id")
	@ManyToOne
	@FormField (entityReferenceName="supplier",optionItemName="name",type="dynamiclist")
	private Supplier supplier;
	@Column
	@FormField
	private String type;
	@Column(name="transaction_date")
	@FormField(type = "date")
	private Date transactionDate;
	@JoinColumn(name = "user_id")
	@ManyToOne
	@FormField (entityReferenceName="user",optionItemName="username",type="dynamiclist")
	private User user;
	@OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	@JsonIgnore
	private List<ProductFlow> productFlows = new ArrayList<>();

}
