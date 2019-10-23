package com.fajar.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {

	public String type() default "text";

	public boolean required() default true;

	public String lableName() default "";

	public String optionItemName() default "";

	public String entityReferenceName() default "";

	public String defaultValue() default "";
}
