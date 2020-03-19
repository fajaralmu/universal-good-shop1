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
@Table (name="cost")
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class Cost extends BaseEntity implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 4969863194918869183L;
	@FormField 
	@Column(unique = true)
	private String name;
	@FormField ( type= FormField.FIELD_TYPE_TEXTAREA) 
	@Column
	private String description;
}
