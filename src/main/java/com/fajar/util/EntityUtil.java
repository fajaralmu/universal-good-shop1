package com.fajar.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Id;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.config.EntityElement;
import com.fajar.config.EntityProperty;
import com.fajar.entity.BaseEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityUtil {

	public static EntityProperty createEntityProperty(String entityName, HashMap<String, Object> listObject) {
		EntityProperty entityProperty = EntityProperty.builder().entityName(entityName.toLowerCase()).build();
		try {
			Class clazz = Class.forName("com.fajar.entity." + entityName);
			if (clazz == null || clazz.getAnnotation(Dto.class) == null) {
				return null;
			}
			List<Field> fieldList = getDeclaredFields(clazz);
			List<EntityElement> entityElements = new ArrayList<>();
			List<String> fieldNames = new ArrayList<>();
			for (Field field : fieldList) {

				FormField formField = field.getAnnotation(FormField.class);
				if (formField == null) {
					continue;
				}

				EntityElement entityElement = new EntityElement();
				boolean isId = field.getAnnotation(Id.class) != null;
				if (isId) {
					entityProperty.setIdField(field.getName());
				}
				String lableName = field.getName();
				if (!formField.lableName().equals("")) {
					lableName = formField.lableName();
				}
				String fieldType = formField.type();
				entityElement.setId(field.getName());
				if (fieldType.equals("") || fieldType.equals("text")) {
					if (isNumber(field)) {
						fieldType = "number";
					}
				} else if (fieldType.equals("img")) {
					entityProperty.getImageElements().add(entityElement.getId());
				}

				fieldNames.add(field.getName());
				entityElement.setIdentity(isId);
				entityElement.setLableName(lableName.toUpperCase());
				entityElement.setRequired(formField.required());
				entityElement.setType(isId ? "hidden" : fieldType);
				entityElement.setClassName(field.getType().getCanonicalName());
				if (!formField.entityReferenceName().equals("") && fieldType.equals("fixedlist")
						&& listObject != null) {

					Class referenceEntityClass = field.getType();
					Field idField = getIdField(referenceEntityClass);
					entityElement.setOptionValueName(idField.getName());
					entityElement.setOptionItemName(formField.optionItemName());

					List<BaseEntity> referenceEntityList = (List<BaseEntity>) listObject
							.get(formField.entityReferenceName());
					if (referenceEntityList != null) {
						entityElement.setOptions(referenceEntityList);
						entityElement.setJsonList(JSONUtil.listToJson(referenceEntityList));
					}

				} else if (!formField.entityReferenceName().equals("") && fieldType.equals("dynamiclist")) {
					Class referenceEntityClass = field.getType();
					Field idField = getIdField(referenceEntityClass);
					entityElement.setOptionValueName(idField.getName());
					entityElement.setOptionItemName(formField.optionItemName());
					entityElement.setEntityReferenceClass(referenceEntityClass.getSimpleName());
				}

				if (field.getType().equals(Date.class)) {
					entityProperty.getDateElements().add(entityElement.getId());
				}
				entityElements.add(entityElement);
			}
			entityProperty.setDateElementsJson(JSONUtil.listToJson(entityProperty.getDateElements()));
			entityProperty.setImageElementsJson(JSONUtil.listToJson(entityProperty.getImageElements()));
			entityProperty.setElements(entityElements);

			entityProperty.setFieldNames(JSONUtil.listToJson(fieldNames));
			log.info("============ENTITY PROPERTY: {} ", entityProperty);
			return entityProperty;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field getDeclaredField(Class clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			if (field == null) {

			}
			return field;
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		if (clazz.getSuperclass() != null) {
			try {
				System.out.println("TRY ACCESS SUPERCLASS");
				return clazz.getSuperclass().getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				System.out.println("FAILED Getting FIELD: " + fieldName);
				e.printStackTrace();
			}
		}

		return null;
	}

	public static List<Field> getDeclaredFields(Class clazz) {
		Field[] baseField = clazz.getDeclaredFields();

		List<EntityElement> entityElements = new ArrayList<EntityElement>();
		List<Field> fieldList = new ArrayList<>();
		for (Field field : baseField) {
			fieldList.add(field);
		}
		if (clazz.getSuperclass() != null) {
			Field[] parentFields = clazz.getSuperclass().getDeclaredFields();
			for (Field field : parentFields) {
				fieldList.add(field);
			}

		}
		return fieldList;
	}

	public static Field getIdField(Class clazz) {
		if (clazz.getAnnotation(Dto.class) == null) {
			return null;
		}
		List<Field> fields = getDeclaredFields(clazz);
		for (Field field : fields) {
			if (field.getAnnotation(Id.class) != null) {
				return field;
			}
		}

		return null;
	}

	public static boolean isNumber(Field field) {
		return field.getType().equals(Integer.class) || field.getType().equals(Double.class)
				|| field.getType().equals(Long.class) || field.getType().equals(BigDecimal.class)
				|| field.getType().equals(BigInteger.class);
	}

	public static void maiXXn(String[] ss) {
		EntityProperty properties = createEntityProperty("Product", null);
		List<EntityElement> elements = properties.getElements();
		for (EntityElement entityElement : elements) {
			System.out.println(entityElement);
		}
	}

	public static Object copyFieldElementProperty(Object source, Class targetClass, boolean withId) {
		System.out.println("CLASSSS :" + targetClass.getCanonicalName());
		Object targetObject = null;
		try {
			targetObject = targetClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Field> fields = getDeclaredFields(source.getClass());
		for (Field field : fields) {
			FormField formField = field.getAnnotation(FormField.class);
			if (formField != null) {
				if (field.getAnnotation(Id.class) != null && !withId) {
					continue;
				}

				Field currentField = getDeclaredField(targetClass, field.getName());
				currentField.setAccessible(true);
				field.setAccessible(true);
				try {
					currentField.set(targetObject, field.get(source));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return targetObject;
	}

	public static List<BaseEntity> validateDefaultValue(List<BaseEntity> entities) {
		for (BaseEntity baseEntity : entities) {
			List<Field> fields = EntityUtil.getDeclaredFields(baseEntity.getClass());
			for (Field field : fields) {
				FormField formField = field.getAnnotation(FormField.class);
				if (field.getType().equals(String.class) && formField != null
						&& formField.defaultValue().equals("") == false) {
					field.setAccessible(true);
					try {
						Object value = field.get(baseEntity);
						if (value == null || value.toString().equals("")) {
							field.set(baseEntity, formField.defaultValue());
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return entities;
	}

}
