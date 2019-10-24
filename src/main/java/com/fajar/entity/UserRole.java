package com.fajar.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.annotation.Dto;

@Dto
@Entity
@Table(name="user_role")
public class UserRole extends BaseEntity  implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -725487831020131248L;
	@Column(unique = true)
	private String name;
	@Column
	private String access;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	
}
