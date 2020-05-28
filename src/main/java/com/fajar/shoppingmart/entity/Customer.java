package com.fajar.shoppingmart.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name = "customer")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = -8365369638070739369L;
	@Column(unique = true)
	@FormField
	private String username;
	@Column(name = "display_name")
	@FormField
	private String name;
	@Column
	@FormField(required = false, type = FieldType.FIELD_TYPE_TEXTAREA)
	private String address;
	@Column
	@FormField
	private String phone;
	@Column
	private String type;

	@Column
	@FormField
	private String email;

}
