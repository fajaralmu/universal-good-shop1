package com.fajar.service;

import com.fajar.annotation.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this class is autowired via XML
 * @author Republic Of Gamers
 *
 */
@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebAppConfiguration {
	
	private String basePage;
	private String uploadedImageRealPath;
	private String uploadedImagePath;
	 
	
}
