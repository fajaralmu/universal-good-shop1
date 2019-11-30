package com.fajar.entity;

import java.lang.reflect.Field;

public class TableNames {
	
	public static String tableNameCategory = "category";

	public TableNames() {
		
	}
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = TableNames.class.getDeclaredField("tableNameCategory");
		field.setAccessible(true);
TableNames xx = new TableNames();
		field.set(xx, "FAJAR");
		System.out.println(field.get( xx));
		
		
	}
}
