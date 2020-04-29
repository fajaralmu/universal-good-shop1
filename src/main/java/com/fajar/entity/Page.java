package com.fajar.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name="page")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Page extends BaseEntity implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -4180675906997901285L;

	@FormField
	@Column
	private String code;
	@FormField
	@Column
	private String name;
	@FormField(lableName = "Authorized (1 or 0)",type = FormField.FIELD_TYPE_NUMBER)
	@Column(nullable = false)
	private int authorized; 
//	@FormField
//	@Column
//	private String link;
	@FormField(type = FormField.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	@FormField(type = FormField.FIELD_TYPE_IMAGE,  required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name= "image_url")
	private String imageUrl;
	
	
	@Transient
	private List<Menu> menus;
}
