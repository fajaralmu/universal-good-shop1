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
@Table (name="registered_request")
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredRequest extends BaseEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -2584171097698972770L; 
	@Column(name="request_id")
	@FormField(required = true)
	private String requestId;
	@Column(name="value")
	@FormField(required = true)
	private String value;
	
}
