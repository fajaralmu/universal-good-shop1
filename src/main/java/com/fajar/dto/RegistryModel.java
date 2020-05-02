package com.fajar.dto;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import com.fajar.entity.User;

 
public class RegistryModel extends UnicastRemoteObject   {
	public RegistryModel() throws RemoteException {
		
		super();
		// TODO Auto-generated constructor stub
	}
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
