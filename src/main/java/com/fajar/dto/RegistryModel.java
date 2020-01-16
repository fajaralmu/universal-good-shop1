package com.fajar.dto;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.HashMap;

import com.fajar.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryModel implements Remote  {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 3868645032944633878L;
	private User user;
	private HashMap<String, Object> tokens;
	private String userToken;

}
