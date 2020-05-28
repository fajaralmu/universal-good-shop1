package com.fajar.shoppingmart.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyValue implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -1668484384625090190L;

	private Object key;
	private Object value;
	@Builder.Default
	private boolean valid = true;
	
}

