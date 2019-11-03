package com.fajar.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopProfile extends BaseEntity implements Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 4095664637854922384L;
	@Column(unique = true)
	@FormField 
	private String name;
	@Column(name="mart_code", unique = true)
	@FormField
	private String martCode;
	@Column(name="short_description")
	@FormField (type="textarea")
	private String shortDescription;
	@Column
	@FormField (type="textarea")
	private String about;
	@Column(name="welcoming_message")
	@FormField (type="textarea")
	private String welcomingMessage;
	@Column
	@FormField (type="textarea")
	private String address;
	@FormField (type="color")
	private String color;
	@Column
	@FormField (type="textarea")
	private String contact;
	@Column
	@FormField
	private String website;
	@FormField(type = "img", required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	
}
