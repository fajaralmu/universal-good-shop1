package com.fajar.shoppingmart.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fajar.shoppingmart.dto.FormInputColumn;
import com.fajar.shoppingmart.service.entity.BaseEntityUpdateService;
import com.fajar.shoppingmart.service.entity.CommonUpdateService;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.TYPE)  
public @interface Dto {

	FormInputColumn formInputColumn() default FormInputColumn.TWO_COLUMN;
	boolean ignoreBaseField() default true;
	boolean editable() default true;
	String value() default "";
	boolean quistionare() default false;
	Class<? extends BaseEntityUpdateService> updateService() default CommonUpdateService.class;
	public boolean commonManagementPage() default true; 
	 
}
