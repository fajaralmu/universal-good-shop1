package com.fajar.entity;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name="cost_flow")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostFlow extends BaseEntity implements Remote, Serializable{

	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6143899665323318955L;
	@Column 
	@FormField(type = FormField.FIELD_TYPE_DATE)
	private Date date;
	@Column 
	@FormField(type = FormField.FIELD_TYPE_TEXTAREA)
	private String description; 
	@JoinColumn(name="cost_id")
	@ManyToOne
	@FormField(entityReferenceName="cost",type=FormField.FIELD_TYPE_FIXED_LIST,optionItemName="name")
	private Cost costType;
	 
	 
}