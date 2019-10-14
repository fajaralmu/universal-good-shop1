package com.fajar.dto;

import java.io.Serializable;

public class RealtimeRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6891178168583718796L;
	private Entity entity;
	
	public Entity getEntity() {
		return entity;
	}
	
	

	public void setEntity(Entity user) {
		this.entity = user;
	}

	@Override
	public String toString() {
		return "ChatRequest [user=" + entity + "]";
	}
	
	
	
	
}
