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
	private User user;
	@Default
//	@Getter(value=AccessLevel.NONE)
//	@Setter(value=AccessLevel.NONE)
	private Map<String, String> cookieValues = new HashMap<>();
	private String requestKey;
	 
	public void addPageCookies(String pageId, String cookie) {
		log.info("addPageCookies, pageId:{}, cookie:{}", pageId, cookie);
		cookieValues.put(pageId, cookie);
	}
	
	public boolean hasCookie(String pageID) {
		return cookieValues.get(pageID) != null;
	}
	public String getCookie(String pageID) {
		return cookieValues.get(pageID);
	}
	
	public UserSessionModel(User user, String requestToken) {
		this.user = SerializationUtils.clone(user);
		this.requestKey=(requestToken);
	}
	
	 
	
	

}
