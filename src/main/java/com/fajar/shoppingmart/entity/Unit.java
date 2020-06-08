package com.fajar.shoppingmart.entity;

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
@Table(name = "unit")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Unit extends BaseEntity {
	/**
	* 
	*/
	private static final long serialVersionUID = -7685706013251246214L;

	@FormField
	@Column(unique = true)
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
}
