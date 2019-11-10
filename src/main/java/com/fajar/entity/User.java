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
@Table
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896877759244837620L;
	@Column(unique = true)
	@FormField
	private String username;
	@Column(name="display_name")
	@FormField
	private String displayName;
	@Column
	@FormField
//	@JsonIgnore
	private String password;
	@JoinColumn(name="role_id")
	@ManyToOne
	@FormField(entityReferenceName="userRole",type="fixedlist",optionItemName="name")
	private UserRole role;
	 
}
