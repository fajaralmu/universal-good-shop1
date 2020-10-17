package com.fajar.shoppingmart.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionMode {

	REGULAR, RETURN;
	
	@JsonCreator
	public static TransactionMode forValue(String value) {
		TransactionMode[] enumKeys = values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].toString().equals(value)) {
				return enumKeys[i];
			}
		}

		return null;
	}

	@JsonValue
	public String toValue() {
		TransactionMode[] enumKeys = values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].equals(this)) {
				return enumKeys[i].toString();
			}
		}

		return null; // or fail
	}
}
