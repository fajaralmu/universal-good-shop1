package com.fajar.shoppingmart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name = "user_role")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -725487831020131248L;
	@Column(unique = true)
	@FormField
	private String name;
	@Column(unique = true)
	@FormField
	private String code;
	@Column
	@FormField
	private String access;

}
