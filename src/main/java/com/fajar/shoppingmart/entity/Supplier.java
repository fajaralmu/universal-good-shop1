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
@Table(name="supplier")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Supplier extends BaseEntity {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 4095664637854922384L;
	@Column(unique = true)
	@FormField 
	private String name;
	@Column
	@FormField (type=FieldType.FIELD_TYPE_TEXTAREA)
	private String address;
	@Column
	@FormField (type=FieldType.FIELD_TYPE_TEXTAREA)
	private String contact;
	@Column
	@FormField
	private String website;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	
}
