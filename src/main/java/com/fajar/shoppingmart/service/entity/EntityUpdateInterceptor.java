package com.fajar.shoppingmart.service.entity;

import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Menu;

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