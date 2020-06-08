package com.fajar.shoppingmart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "product_image")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5778224979175269179L;
	@FormField
	@Column
	private String code;
	@FormField
	@Column
	private String name;
	@FormField
	@Column
	private String url;
	@JoinColumn(name = "category_id", nullable = true)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Product product;

}
