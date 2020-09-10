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
	// TODO: remove
	@FormField
	@Column
	private String page;
	@JoinColumn(name = "page_id", nullable = false)
	@ManyToOne
	@FormField(lableName = "Page", type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private Page menuPage;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	
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
