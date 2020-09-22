package com.fajar.shoppingmart.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.dto.FormInputColumn;
import com.fajar.shoppingmart.service.entity.ProductUpdateService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN, updateService =  ProductUpdateService.class)
@Entity
@Table(name = "product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Product extends BaseEntity {

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
	@Type(type = "org.hibernate.type.LongType")
	private long price;
	@Column
	@FormField
	private String type;
	
	@Column(name = "image_url", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, multipleImage = true, defaultValue = "Default.BMP")
	private String imageUrl; // type:BLOB
	
	@JoinColumn(name = "unit_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Unit unit;
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Category category;

	@Transient
	private boolean newProduct;
	@Transient
	@Setter(value = AccessLevel.NONE)
	private int count;
	@Transient
	private List<Supplier> suppliers;
	
	public void setCount(int count) {
		log.debug(this.name+" Count: "+ count);
		this.count = count;
	}

}
