package com.fajar.entity.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fajar.annotation.Dto;
import com.fajar.util.JSONUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Dto
public class EntityProperty implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 2648801606702528928L;
	private String entityName;
	private List<EntityElement> elements;
	private String fieldNames;
	private String idField;
	@Builder.Default
	private boolean editable = true;
	@Builder.Default
	private boolean withDetail = false;
	private String detailFieldName;
	@Builder.Default
	private List<String> dateElements = new ArrayList<String>();
	private String dateElementsJson;
	@Builder.Default
	private List<String> imageElements = new ArrayList<String>();
	private String imageElementsJson;
	@Builder.Default
	private List<String> currencyElements = new ArrayList<String>();
	private String currencyElementsJson;
	
	public void setElementJsonList() { 
		
		this.dateElementsJson = JSONUtil.listToJson(dateElements);
		this.imageElementsJson = JSONUtil.listToJson(imageElements);
		this.currencyElementsJson = JSONUtil.listToJson(currencyElements);
	}
 	

}
