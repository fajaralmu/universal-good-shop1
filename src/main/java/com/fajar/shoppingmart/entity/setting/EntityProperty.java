package com.fajar.shoppingmart.entity.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.util.EntityUtil;
import com.fajar.shoppingmart.util.MyJsonUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Dto
@Slf4j
public class EntityProperty implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 2648801606702528928L;
	private String entityName;
	private String alias;
	private String fieldNames; 
	
	private String idField;
	private int formInputColumn;
	@Builder.Default
	private boolean editable = true;
	@Builder.Default
	private boolean withDetail = false;
	private String detailFieldName;
	
	private String imageElementsJson;
	private String dateElementsJson;
	private String currencyElementsJson;
	
	@Builder.Default
	private List<String> dateElements = new ArrayList<String>();
	@Builder.Default
	private List<String> imageElements = new ArrayList<String>(); 
	@Builder.Default
	private List<String> currencyElements = new ArrayList<String>();
	private List<EntityElement> elements;
	private List<String> fieldNameList;
	
	private boolean ignoreBaseField;
	

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

	public void determineIdField() {
		if(null == elements) {
			log.error("Entity ELements is NULL");
			return;
		}
		log.info("elements size: {}", elements.size());
		
		for(EntityElement entityElement : elements) {
			log.info("entityElement.name: {}", entityElement.getId());
			if(entityElement.isIdField() && getIdField() == null) {
				setIdField(entityElement.getId());
				log.info("Got ID field: {}", entityElement.getId());
				return;
			}
		}
		
		log.warn("NO ID FIELD FOR THIS ENTITY: {}", entityName);
	}
	
	public static void main(String[] args) {
		EntityProperty prop = EntityUtil.createEntityProperty(Product.class, null);
		System.out.println("ID FIELD: "+prop.getIdField());
	}

}
