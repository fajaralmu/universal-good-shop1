package com.fajar.dto;

import java.io.Serializable;

import com.fajar.annotation.Dto;
import com.fajar.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTempRequest implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 3786301144797644660L;
	 
	private User user;
	private Long userId;
	private String requestURI;
	

}
