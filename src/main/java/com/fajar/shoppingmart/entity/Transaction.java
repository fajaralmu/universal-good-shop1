package com.fajar.shoppingmart.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.service.entity.BaseEntityUpdateService;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(updateService = BaseEntityUpdateService.class)
@Entity
@Table(name="transaction")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity {

	/**
	* 
	*/
	private static final long serialVersionUID = -5995699726278991966L;

	@JoinColumn(name = "customer_id")
	@ManyToOne
	@FormField(  optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Customer customer;
	
	@JoinColumn(name = "supplier_id")
	@ManyToOne
	@FormField(  optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Supplier supplier;
	
	@Column(unique = true)
	@FormField
	private String code;
	
	@Column
	@Enumerated(EnumType.STRING)
	@FormField
	private TransactionType type;
	
	@Column(name = "transaction_date")
	@FormField(type = FieldType.FIELD_TYPE_DATE)
	private Date transactionDate;
	
	@JoinColumn(name = "user_id")
	@ManyToOne
	@FormField(  optionItemName = "username", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private User user;
	//@OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL/* , fetch = FetchType.LAZY */)
	@Transient
	@Builder.Default
	@JsonIgnore
	private List<ProductFlow> productFlows = new ArrayList<>();

	@Builder.Default
	@Transient
	@FormField(showDetail = true, lableName = "Product Flow", optionItemName = "code", detailFields = {
			"transaction.code", "id", "expiryDate", "product.name", "count", "price", "totalPrice", })
	private Object productFlow = "See Detail";

	@Override
	public String toString() {

		return "Transaction [customer=" + customer + ", supplier=" + supplier + ", code=" + code + ", type=" + type
				+ ", transactionDate=" + transactionDate + ", user=" + user + ", productFlows="
				+ (productFlows == null ? "0" : productFlows.size()) + "]";
	}

}
