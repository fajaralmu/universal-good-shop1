package com.fajar.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.entity.Menu.MenuBuilder;

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
public class Customer extends BaseEntity implements Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = -8365369638070739369L;
	@Column(unique = true)
	@FormField 
	private String username;
	@Column(name="display_name")
	@FormField 
	private String name;
	@Column
	@FormField(required=false, type="textarea") 
	private String address;
	@Column
	@FormField 
	private String phone;
	@Column 
	private String type;
	
	@Column
	@FormField 
	private String email;

	 
	
	
	
}
