package com.fajar.shoppingmart.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

import com.fajar.shoppingmart.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

 @Data
 @Builder
 @AllArgsConstructor
 @Slf4j
 @NoArgsConstructor
public class UserSessionModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3868645032944633878L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 3868645032944633878L;
	private User user;private String requestKey;
	private String jwt;
	 
	
	public UserSessionModel(User user, String requestToken) {
		this.user = SerializationUtils.clone(user);
		this.requestKey=(requestToken);
	}
	
	 
	
	

}
