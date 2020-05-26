package com.fajar.service.entity;

import com.fajar.entity.BaseEntity;
import com.fajar.entity.Menu;

public interface EntityUpdateInterceptor {
	
	public void preUpdate(BaseEntity baseEntity) ;

	
	/**
	 * =======================================
	 *          Static Methods
	 * =======================================
	 */
	public static EntityUpdateInterceptor menuInterceptor() { 
		return new EntityUpdateInterceptor() {
			
			@Override
			public void preUpdate(BaseEntity baseEntity) { 
				Menu menu = (Menu) baseEntity;
				if(menu.getUrl().startsWith("/") == false) {
					menu.setUrl("/"+menu.getUrl());
				}
			}
		};
	}
	
}
