package com.fajar.util;

import static com.fajar.annotation.FormField.FIELD_TYPE_CURRENCY;
import static com.fajar.annotation.FormField.FIELD_TYPE_DYNAMIC_LIST;
import static com.fajar.annotation.FormField.FIELD_TYPE_FIXED_LIST;
import static com.fajar.annotation.FormField.FIELD_TYPE_HIDDEN;
import static com.fajar.annotation.FormField.FIELD_TYPE_IMAGE;
import static com.fajar.annotation.FormField.FIELD_TYPE_NUMBER;
import static com.fajar.annotation.FormField.FIELD_TYPE_TEXT;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.setting.EntityElement;
import com.fajar.entity.setting.EntityProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityUtil {

	
	public static EntityProperty createEntityProperty(Class clazz, HashMap<String, List<BaseEntity>> listObject) {
		if (clazz == null || clazz.getAnnotation(Dto.class) == null) {
			return null;
		}
		
		Dto dto = (Dto) clazz.getAnnotation(Dto.class);
		
		EntityProperty entityProperty = EntityProperty.builder().entityName(clazz.getSimpleName().toLowerCase()).build();
		try {

			List<Field> fieldList 				= getDeclaredFields(clazz);
			List<EntityElement> entityElements 	= new ArrayList<>();
			List<String> fieldNames 			= new ArrayList<>();
			List<String> dateElements 			= new ArrayList<>();
			String fieldToShowDetail 			= "";
			
			for (Field field : fieldList) {

				FormField formField = field.getAnnotation(FormField.class);
				
				if (formField == null) {
					continue;
				}

				boolean isIdField = field.getAnnotation(Id.class) != null;
				
				if (isIdField) {
					entityProperty.setIdField(field.getName());
				}
				
				String lableName = field.getName();
				String fieldType = formField.type();
				
				if (!formField.lableName().equals("")) {
					lableName = formField.lableName();
				}  
				
				final String entityElementId = field.getName();
				
				if (fieldType.equals("") || fieldType.equals(FIELD_TYPE_TEXT)) {
					if (isNumber(field)) {
						fieldType = FIELD_TYPE_NUMBER;
					}
					
				} else if (fieldType.equals(FIELD_TYPE_IMAGE)) {
					entityProperty.getImageElements().add(entityElementId);
					
				}else if (fieldType.equals(FIELD_TYPE_CURRENCY)) {
					entityProperty.getCurrencyElements().add(entityElementId);
					fieldType = FIELD_TYPE_NUMBER;
				}

				/**
				 * add field names
				 */
				fieldNames.add(field.getName());
				
				final EntityElement entityElement = new EntityElement();
				
				entityElement.setId(entityElementId );
				entityElement.setIdentity(isIdField);
				entityElement.setLableName(lableName.toUpperCase());
				entityElement.setRequired(formField.required());
				entityElement.setType(isIdField ? FIELD_TYPE_HIDDEN : fieldType);
				entityElement.setMultiple(formField.multiple());
				entityElement.setClassName(field.getType().getCanonicalName());
				entityElement.setShowDetail(formField.showDetail());
				
				if (formField.detailFields().length > 0) {
					entityElement.setDetailFields(String.join("~", formField.detailFields()));
				}
				if (formField.showDetail()) {
					entityElement.setOptionItemName(formField.optionItemName()); 
					fieldToShowDetail = field.getName();
				}
				
				boolean referenceNameExist = !formField.entityReferenceName().equals("");

				/**
				 * IF has entity reference (JOIN COLUMN)
				 */
				if (referenceNameExist) {
					
					Class referenceEntityClass = field.getType();
					Field idField = getIdField(referenceEntityClass);
					
					if(idField == null) continue; 
					
					entityElement.setOptionValueName(idField.getName());
					entityElement.setOptionItemName(formField.optionItemName());
					 
					
					if(fieldType.equals(FIELD_TYPE_FIXED_LIST) && listObject != null) {  
	
						List<BaseEntity> referenceEntityList = (List<BaseEntity>) listObject.get(formField.entityReferenceName());
						
						if (referenceEntityList != null) {
							entityElement.setOptions(referenceEntityList);
							entityElement.setJsonList(MyJsonUtil.listToJson(referenceEntityList));
						}
	
					} else if (fieldType.equals(FIELD_TYPE_DYNAMIC_LIST)) {
						 
						entityElement.setEntityReferenceClass(referenceEntityClass.getSimpleName());
					}
					
				}
				if (field.getType().equals(Date.class) && field.getAnnotation(JsonFormat.class) == null) {
					dateElements.add(entityElement.getId());
				}
				
				entityElements.add(entityElement);
			}
			entityProperty.setElementJsonList();
			entityProperty.setElements(entityElements);
			entityProperty.setDetailFieldName(fieldToShowDetail);
			entityProperty.setDateElements(dateElements);
			entityProperty.setDateElementsJson(MyJsonUtil.listToJson(dateElements));
			entityProperty.setFieldNames(MyJsonUtil.listToJson(fieldNames));
			entityProperty.setFieldNameList(fieldNames);
			entityProperty.setFormInputColumn(dto.formInputColumn().value);
			
			log.info("============ENTITY PROPERTY: {} ", entityProperty);
			
			return entityProperty;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T getClassAnnotation(Class<?> entityClass, Class annotation) {
		try {
			return (T) entityClass.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}
	
	

	public static <T> T getFieldAnnotation(Field field, Class annotation) {
		try {
			return (T) field.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Field getDeclaredField(Class clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			if (field == null) {

			}
			field.setAccessible(true);
			return field;

		} catch (Exception e) {
			log.error("Error get declared field in the class, and try access super class");
		}
		if (clazz.getSuperclass() != null) {

			try {
				log.info("TRY ACCESS SUPERCLASS");
				
				Field superClassField = clazz.getSuperclass().getDeclaredField(fieldName);
				superClassField.setAccessible(true);
				return superClassField;
			} catch (Exception e) {
				 
				log.error("FAILED Getting FIELD: " + fieldName);
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
		log.info("Get ID FIELD FROM :" + clazz.getCanonicalName());

		if (clazz.getAnnotation(Entity.class) == null) {
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

	/**
	 * copy object with option ID included or NOT
	 * 
	 * @param source
	 * @param targetClass
	 * @param withId
	 * @return
	 */
	public static BaseEntity copyFieldElementProperty(BaseEntity source, Class<? extends BaseEntity> targetClass, boolean withId) {
		log.info("Will Copy Class :" + targetClass.getCanonicalName());

		BaseEntity targetObject = null;
		try {
			targetObject = (BaseEntity) targetClass.newInstance();

		} catch (Exception e) {
			log.error("Error when create instance");
			e.printStackTrace();
		}
		List<Field> fields = getDeclaredFields(source.getClass());

		for (Field field : fields) {

			if (field.getAnnotation(Id.class) != null && !withId) {
				continue;
			}  
			
			Field currentField = getDeclaredField(targetClass, field.getName());

			if (currentField == null)
				continue;

			currentField.setAccessible(true);
			field.setAccessible(true);

			try {
				currentField.set(targetObject, field.get(source));

			} catch (Exception e) {
				log.error("Error set new value");
				e.printStackTrace();
			}

		} 
		
		if(targetObject.getCreatedDate() == null) {
			targetObject.setCreatedDate(new Date());
		}
		targetObject.setModifiedDate(new Date());
		
		return targetObject;
	}

	public static void validateDefaultValues(List<? extends BaseEntity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			validateDefaultValue(entities.get(i));
		}
	}
	
	public static <T extends BaseEntity> T validateDefaultValue(BaseEntity baseEntity) {
		List<Field> fields = EntityUtil.getDeclaredFields(baseEntity.getClass());
		
		for (Field field : fields) {
			
			try {

				field.setAccessible(true);
				FormField formField = field.getAnnotation(FormField.class);

				if (field.getType().equals(String.class) && formField != null
						&& formField.defaultValue().equals("") == false) {

					Object value = field.get(baseEntity);

					if (value == null || value.toString().equals("")) {
						field.set(baseEntity, formField.defaultValue());
					}

				}

				if (formField != null && formField.multiply().length > 1) {
					Object objectValue = field.get(baseEntity);

					if (objectValue != null)
						continue;

					Object newValue = "1";
					String[] multiplyFields = formField.multiply();

					loop: for (String multiplyFieldName : multiplyFields) {

						Field multiplyField = getDeclaredField(baseEntity.getClass(), multiplyFieldName);

						if (multiplyField == null) {
							continue loop;
						}
						multiplyField.setAccessible(true);

						Object multiplyFieldValue = multiplyField.get(baseEntity);
						String strVal = "0";

						if (multiplyFieldValue != null) {
							strVal = multiplyFieldValue.toString();
						}

						if (field.getType().equals(Long.class)) {
							newValue = Long.parseLong(newValue.toString()) * Long.parseLong(strVal);

						} else if (field.getType().equals(Integer.class)) {
							newValue = Integer.parseInt(newValue.toString()) * Integer.parseInt(strVal);

						} else if (field.getType().equals(Double.class)) {
							newValue = Double.parseDouble(newValue.toString()) * Double.parseDouble(strVal);
						}

					}
					field.set(baseEntity, newValue);

				}
			} catch (Exception e) {
				log.error("Error validating field, will conitnue loop");
				e.printStackTrace();
			}
		}
		return (T) baseEntity;
	}

	public static <T extends Collection<? extends BaseEntity>> T validateDefaultValue(List<BaseEntity> entities) {
		for (BaseEntity baseEntity : entities) {
			baseEntity = validateDefaultValue(baseEntity);
		}
		return (T) entities;
	}

	public static <T> T getObjectFromListByFieldName(final String fieldName, final Object value, final List list) {

		for (Object object : list) {
			Field field = EntityUtil.getDeclaredField(object.getClass(), fieldName);
			field.setAccessible(true);
			try {
				Object fieldValue = field.get(object);

				if (fieldValue != null && fieldValue.equals(value)) {
					return (T) object;
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return null;
	}

	public static boolean existInList(Object o, List l) {
		for (Object object : l) {
			if (object.equals(o)) {
				return true;
			}
		}
		return false;
	}

}
