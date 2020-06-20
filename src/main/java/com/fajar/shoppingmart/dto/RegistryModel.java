package com.fajar.shoppingmart.dto;

import java.rmi.Remote;
import java.util.HashMap;

import com.fajar.shoppingmart.entity.User;

 
public class RegistryModel implements Remote{
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public HashMap<String, Object> getTokens() {
		return tokens;
	}
	public void setTokens(HashMap<String, Object> tokens) {
		this.tokens = tokens;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
