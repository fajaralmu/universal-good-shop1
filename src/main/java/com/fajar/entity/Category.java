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
public class Category extends BaseEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -1168912843978053906L; 
	@FormField 
	@Column(unique = true)
	private String name;
	@FormField ( type="textarea") 
	@Column
	private String description;
}
