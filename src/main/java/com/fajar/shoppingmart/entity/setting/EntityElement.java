package com.fajar.shoppingmart.entity.setting;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.fajar.shoppingmart.annotation.BaseField;
import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.util.EntityUtil;
import com.fajar.shoppingmart.util.MyJsonUtil;
import com.fajar.shoppingmart.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Dto
@Slf4j
public class EntityElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6768302238247458766L;
	private String id;
	private String type;
	private String className;
	private boolean identity;
	private boolean required;
	private boolean idField;
	private String lableName;
	private List<BaseEntity> options;
	private String jsonList;
	private String optionItemName;
	private String optionValueName;
	private String entityReferenceName;
	private String entityReferenceClass;
	private boolean multiple;
	private boolean showDetail;
	private String detailFields;
	private String[] defaultValues;
	private List<Object> plainListValues;
	
	private boolean detailField;

	//not shown in view
	
	public final Field field;
	public final boolean ignoreBaseField;
	public EntityProperty entityProperty;
	public Map<String, List> additionalMap; 
	
	private FormField formField;
	private BaseField baseField;
	private boolean skipBaseField;
	private boolean hasJoinColumn;

	public EntityElement(Field field, EntityProperty entityProperty) {
		this.field = field;
		this.ignoreBaseField = entityProperty.isIgnoreBaseField();
		this.entityProperty = entityProperty;
		init();
	}
	public EntityElement(Field field, EntityProperty entityProperty, Map<String, List> additionalMap) {
		this.field = field;
		this.ignoreBaseField = entityProperty.isIgnoreBaseField();
		this.entityProperty = entityProperty;
		this.additionalMap = additionalMap;
		init();
	}
	
	private void init() {
		formField = field.getAnnotation(FormField.class);
		baseField = field.getAnnotation(BaseField.class);
		
		idField = field.getAnnotation(Id.class) != null;
		skipBaseField = !idField && (baseField != null && ignoreBaseField);
		
		identity = idField;
		hasJoinColumn = field.getAnnotation(JoinColumn.class) != null;
	}
	
	public boolean build() {
		boolean result = doBuild();
		setEntityProperty(null);
		return result;
	}

	private boolean doBuild() {
		
		boolean formFieldIsNullOrSkip = (formField == null || skipBaseField);
		if (formFieldIsNullOrSkip) {
			return false;
		} 
		
		String lableName = formField.lableName().equals("") ? field.getName() : formField.lableName(); 
		FieldType fieldType = determineFieldType();
		
		try {
			
			checkFieldType(fieldType); 
			boolean hasJoinColumn = field.getAnnotation(JoinColumn.class) != null; 
			
			if (hasJoinColumn) {
				processJoinColumn(fieldType);  
			}
		} catch (Exception e1) { 
			e1.printStackTrace();
			return false;
		}
 

		checkDetailField();
		setId(field.getName());
		setIdentity(idField);
		setLableName(StringUtil.extractCamelCase(lableName));
		setRequired(formField.required());
		setType(fieldType.value);
		setMultiple(formField.multiple());
		setClassName(field.getType().getCanonicalName());
		setShowDetail(formField.showDetail());
		return true;
	}
	
	private void checkDetailField() {
		
		if (formField.detailFields().length > 0) {
			setDetailFields(String.join("~", formField.detailFields()));
		}
		if (formField.showDetail()) {
			setOptionItemName(formField.optionItemName());
			setDetailField(true);
		}
	}
	private void checkFieldType(FieldType fieldType) throws Exception {
		 
		final String entityElementId = field.getName(); 

		/**
		 * check @FormField.fieldType
		 */
		if (fieldType.equals(FieldType.FIELD_TYPE_IMAGE)) {
			entityProperty.getImageElements().add(entityElementId);

		} else if (fieldType.equals(FieldType.FIELD_TYPE_CURRENCY)) {
			entityProperty.getCurrencyElements().add(entityElementId);

			fieldType = FieldType.FIELD_TYPE_NUMBER;
		} else if (fieldType.equals(FieldType.FIELD_TYPE_DATE)) {
			entityProperty.getDateElements().add(entityElementId);

		} else if (fieldType.equals(FieldType.FIELD_TYPE_PLAIN_LIST)) {
			String[] availableValues = formField.availableValues();
			
			if (availableValues.length > 0) {
				setPlainListValues(Arrays.asList(availableValues));
				
			} else if (field.getType().isEnum()) {
				Object[] enumConstants = field.getType().getEnumConstants();
				setPlainListValues(Arrays.asList(enumConstants));
				
			} else {
				log.error("Ivalid element: {}", field.getName());
				throw new Exception("Invalid Element");
			}
		}

	}
	private FieldType determineFieldType() {
		
		FieldType fieldType;
		
		if (EntityUtil.isNumericField(field)) {
			fieldType = FieldType.FIELD_TYPE_NUMBER;

		} else if (field.getType().equals(Date.class) && field.getAnnotation(JsonFormat.class) == null) {
			fieldType = FieldType.FIELD_TYPE_DATE;

		} else if (idField) {
			fieldType = FieldType.FIELD_TYPE_HIDDEN;
		} else {
			fieldType = formField.type();
		}
		return fieldType;
	}
	
	private void processJoinColumn(FieldType fieldType) throws Exception {
		log.info("field {} of {} is join column", field.getName(), field.getDeclaringClass()); 
		
		Class referenceEntityClass = field.getType();
		Field referenceEntityIdField = EntityUtil.getIdFieldOfAnObject(referenceEntityClass);

		if (referenceEntityIdField == null) {
			throw new Exception("ID Field Not Found");
		}
		
		if (fieldType.equals(FieldType.FIELD_TYPE_FIXED_LIST) && additionalMap != null) {
			
			List<BaseEntity> referenceEntityList = (List<BaseEntity>) additionalMap.get(field.getName()); 
			
			if (referenceEntityList != null) {
				setOptions(referenceEntityList);
				setJsonList(MyJsonUtil.listToJson(referenceEntityList));
			}

		} else if (fieldType.equals(FieldType.FIELD_TYPE_DYNAMIC_LIST)) {

			setEntityReferenceClass(referenceEntityClass.getSimpleName());
		}

		setOptionValueName(referenceEntityIdField.getName());
		setOptionItemName(formField.optionItemName());
	}

}
