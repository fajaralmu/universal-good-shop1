package com.fajar.shoppingmart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.EXPECTATION_FAILED, reason="Invalid Value")   
public class InvalidValueException extends RuntimeException {/**
	 * 
	 */
	private static final long serialVersionUID = -440712686302437231L;
	public InvalidValueException(String message) {
		super(message);
	}
 

}
