package com.fajar.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {

	public String type() default "text";
	
	public boolean showDetail() default false;
	
	//multiple image
	public boolean multiple() default false;

	public boolean required() default true;

	public String lableName() default "";

	public String optionItemName() default "";

	public String entityReferenceName() default "";

	public String defaultValue() default "";

	public String[] detailFields() default {};

	//the value is result of array of fields multiplication
	public String[] multiply() default {};
}
