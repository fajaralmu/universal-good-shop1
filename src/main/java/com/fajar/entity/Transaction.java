package com.fajar.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.dto.FieldType;
import com.fajar.dto.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name="transaction")
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
	@FormField
	private String type;
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
