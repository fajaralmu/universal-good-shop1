package com.fajar.entity.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fajar.annotation.Dto;
import com.fajar.util.MyJsonUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Dto
public class EntityProperty implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 2648801606702528928L;
	private String entityName;
	private List<EntityElement> elements;
	private String fieldNames;
	private List<String> fieldNameList;
	private String idField;
	private int formInputColumn;
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

		this.dateElementsJson 		= MyJsonUtil.listToJson(dateElements);
		this.imageElementsJson 		= MyJsonUtil.listToJson(imageElements);
		this.currencyElementsJson 	= MyJsonUtil.listToJson(currencyElements);
	}

	public void removeElements(String... fieldNames) {
		if(this.elements == null) return;
		for (int i = 0; i < fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			loop:for(String fName: fieldNameList) {
				if(fieldName.equals(fName)) {
					fieldNameList.remove(fName);
					break loop;
				}
			}
			loop2: for (EntityElement entityElement : this.elements) {
				if (entityElement.getId().equals(fieldName)) {
					this.elements.remove(entityElement);
					break loop2;
				}
			}
		}
		this.fieldNames = MyJsonUtil.listToJson(fieldNameList);
	}

}
