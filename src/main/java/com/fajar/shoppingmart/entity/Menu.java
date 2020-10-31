package com.fajar.shoppingmart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.service.entity.EntityUpdateInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(ignoreBaseField = false)
@Entity
@Table(name = "menu")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu extends BaseEntity {
	/**
	* 
	*/
	private static final long serialVersionUID = -6895624969478733293L;

	@FormField
	@Column
	private String code;
	@FormField
	@Column
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	@FormField
	@Column
	private String url;

	@FormField(lableName = "Path Variables Separated by ','", required = false)
	@Column
	private String pathVariables;
	// TODO: remove
//	@FormField
//	@Column
//	private String page;
	@JoinColumn(name = "page_id", nullable = false)
	@ManyToOne
	@FormField(lableName = "Parent Page", type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private Page menuPage;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	
	/**
	 * the return value must be started and ended by double quotes character
	 * @return
	 */
	public String pathVariableAsJson() {
		if(pathVariables == null || pathVariables.isEmpty()) {
			return "\"\"";
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(pathVariables.split(","));
			String jsonStringified = objectMapper.writeValueAsString(json);
			return jsonStringified;// (JSON_Replaced.replace("\"[", "[").replace("]\"", "]"));
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "\"\"";
	}
	
	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String JSON = objectMapper.writeValueAsString("FAJAR,ALMU".split(","));
		String JSON_2 = objectMapper.writeValueAsString(JSON);
		System.out.println(JSON_2.replace("\"[", "[").replace("]\"", "]"));
	}
	
	@PrePersist
	public void pre() {
		if(getColor() == null) {
			setColor("#ccc");
		}
		if(getFontColor() == null) {
			setFontColor("#000000");
		}
	}
	
	@Override
	public EntityUpdateInterceptor modelUpdateInterceptor() {

		return new EntityUpdateInterceptor<Menu>() {

			@Override
			public Menu preUpdate(Menu menu) {
				if (menu.getUrl().startsWith("/") == false) {
					menu.setUrl("/" + menu.getUrl());
				}
				return menu;

			}
		};
	}

}
