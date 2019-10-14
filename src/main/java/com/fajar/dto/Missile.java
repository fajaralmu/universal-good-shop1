package com.fajar.dto;

import java.io.Serializable;

import com.fajar.annotation.Dto;

@Dto
public class Missile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Physical physical;
	private Integer id;
	private Integer entityId;
	public Physical getPhysical() {
		return physical;
	}
	public void setPhysical(Physical entity) {
		this.physical = entity;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getEntityId() {
		return entityId;
	}
	public void setEntityId(Integer userId) {
		this.entityId = userId;
	}
	
	

}
