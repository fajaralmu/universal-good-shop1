package com.fajar.config;

import java.io.Serializable;
import java.util.List;

import com.fajar.annotation.Dto;
import com.fajar.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Dto
public class EntityProperty implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 2648801606702528928L;
	private String entityName;
	private List<EntityElement> elements;
	private String fieldNames;
	private String idField;
	

}
