package com.fajar.entity;

import static com.fajar.dto.FieldType.FIELD_TYPE_TEXTAREA;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.dto.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(ignoreBaseField = false)
@Entity
@Table(name = "shop_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopProfile extends BaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 4095664637854922384L;
	@Column(unique = true)
	@FormField
	private String name;
	@Column(name = "mart_code", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_HIDDEN)
	private String martCode;
	@Column(name = "short_description")
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String shortDescription;
	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String about;
	@Column(name = "welcoming_message")
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String welcomingMessage;
	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String address;

	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String contact;
	@Column
	@FormField
	private String website;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultBackground.BMP")
	@Column(name = "background_url")
	private String backgroundUrl;

}
