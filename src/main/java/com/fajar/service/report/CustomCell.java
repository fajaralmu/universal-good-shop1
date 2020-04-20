package com.fajar.service.report;

import java.io.Serializable;

import lombok.Data;

@Data
public class CustomCell implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1936743863408885308L;
	protected Object value;
	
	@Override
	public String toString() {
		if(null == value) {
			return "";
		}
		
		return value.toString();
	}

}
