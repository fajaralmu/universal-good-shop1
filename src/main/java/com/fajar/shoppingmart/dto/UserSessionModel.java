package com.fajar.shoppingmart.dto;

import java.io.Serializable;
import java.util.HashMap;

import com.fajar.shoppingmart.entity.User;

import lombok.Data;

 @Data
public class UserSessionModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3868645032944633878L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 3868645032944633878L;
	private User user;
	private HashMap<String, Object> tokens;
	private String userToken;
	public UserSessionModel() {
		
	}
	public UserSessionModel(User user, String userToken) {
		this.user = user;
		this.userToken = userToken;
	}
	
	 
	
	

}
