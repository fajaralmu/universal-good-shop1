package com.fajar.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;

@Dto
@Entity
@Table
public class Customer extends BaseEntity implements Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = -8365369638070739369L;
	@Column
	@FormField 
	private String username;
	@Column(name="display_name")
	@FormField 
	private String displayName;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
}
