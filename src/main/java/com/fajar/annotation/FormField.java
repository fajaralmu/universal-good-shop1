package com.fajar.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {

	public static final String FIELD_TYPE_TEXTAREA = "textarea";
	public static final String FIELD_TYPE_DATE = "date";
	
	public String type() default FIELD_TYPE_TEXT;
	
	public boolean showDetail() default false;
	
	//multiple image
	public boolean multiple() default false;

	public boolean required() default true;

	public String lableName() default "";

	public String optionItemName() default "";

	public String entityReferenceName() default "";

	public String defaultValue() default "";

	public String[] detailFields() default {};

	public String[] defaultValues() default {};
	//the value is result of array of fields multiplication
	public String[] multiply() default {};
	
	public static final String FIELD_TYPE_TEXT = "text";
	public static final String FIELD_TYPE_IMAGE = "img";
	public static final String FIELD_TYPE_CURRENCY = "currency";
	public static final String FIELD_TYPE_NUMBER = "number";
	public static final String FIELD_TYPE_HIDDEN = "hidden";
	public static final String FIELD_TYPE_COLOR = "color";
	public static final String FIELD_TYPE_FIXED_LIST ="fixedlist";
	public static final String FIELD_TYPE_DYNAMIC_LIST = "dynamiclist";

}
