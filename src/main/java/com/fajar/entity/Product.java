package com.fajar.entity;

import java.io.Serializable;
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
import com.fajar.dto.FormInputColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN)
@Entity
@Table(name = "product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(unique = true)
	@FormField(lableName = "Product Code")
	private String code;
	@Column(unique = true)
	@FormField
	private String name;
	@Column
	@FormField
	private String description;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_CURRENCY)
	private long price;
	@Column
	@FormField
	private String type;
	@Column(name = "image_url", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, multiple = true, defaultValue = "Default.BMP")
	private String imageUrl;
	@JoinColumn(name = "unit_id")
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Unit unit;
	@JoinColumn(name = "category_id", nullable = true)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Category category;

	@Transient
	private boolean newProduct;
	@Transient
	private int count;
	@Transient
	private List<Supplier> suppliers;

}
