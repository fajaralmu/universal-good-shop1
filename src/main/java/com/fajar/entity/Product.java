package com.fajar.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.entity.Unit.UnitBuilder;

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
public class Product extends BaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column
	@FormField 
	private String name;
	@Column
	@FormField 
	private String description;
	@Column
	@FormField 
	private Long price;
	@Column
	@FormField 
	private String type;
	@JoinColumn(name = "unit_id")
	@ManyToOne
	@FormField (entityReferenceName="unit",optionItemName="name",type="dynamiclist")
	private Unit unit;

}
