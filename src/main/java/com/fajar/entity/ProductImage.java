package com.fajar.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name="product_image")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage extends BaseEntity implements Serializable {
	 
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
	@FormField (entityReferenceName="category",optionItemName="name",type="dynamiclist")
	private Product product;
	

}
