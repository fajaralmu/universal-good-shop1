package com.fajar.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiRequest.ShopApiRequestBuilder;

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

	@JoinColumn(name = "user_id")
	@ManyToOne
	private User user;
	@JoinColumn(name = "customer_id")
	@ManyToOne
	private Customer customer;
	@JoinColumn(name = "supplier_id")
	@ManyToOne
	private Customer supplier;
	@Column
	private String type;
	@OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
	@Builder.Default
	private List<ProductFlow> productFlows = new ArrayList<>();

}
