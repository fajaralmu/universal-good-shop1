package com.fajar.shoppingmart.querybuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.JoinColumn;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.KeyValue;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CriteriaBuilder {
	
	

	private final Session hibernateSession;

//	private String currentAlias;
	private int joinIndex = 1;
	private final Class<? extends BaseEntity> entityClass;
	private final Filter filter;
	private final Map<String , Integer> aliases = new HashMap<>();
	private final boolean allItemExactSearch;

	public CriteriaBuilder(Session hibernateSession, Class<? extends BaseEntity> _class,  Filter filter) {
		this.hibernateSession = hibernateSession;
		this.entityClass = _class;
		this.filter = SerializationUtils.clone(filter);
		this.allItemExactSearch = filter.isExacts();
	}

	

	public Criterion restrictionEquals(Class<?> entityClass, String fieldName, Object fieldValue) {
		String entityName = entityClass.getSimpleName();
		String columnName;
		boolean multiKey = fieldName.contains(",");
		Field field;
		
		if (multiKey) {
			Field hisField = EntityUtil.getDeclaredField(entityClass, fieldName.split(",")[0]);
			field = EntityUtil.getDeclaredField(hisField.getType(), fieldName.split(",")[1]);
			String alias = getAlias(hisField.getName())+"."+QueryUtil.getColumnName(field); 
			return Restrictions.sqlRestriction(alias+"='"+fieldValue+"'");
		} else {
			Field hisField = EntityUtil.getDeclaredField(entityClass, fieldName);
			
			KeyValue joinColumnResult = QueryUtil.checkIfJoinColumn(fieldName, hisField, true); 
			field = EntityUtil.getDeclaredField(entityClass, fieldName);
			if(null != joinColumnResult) {
				//process join column
				FormField formField = hisField.getAnnotation(FormField.class);
				fieldName=getAlias(fieldName)+"."+formField.optionItemName();
				return Restrictions.sqlRestriction(fieldName+"='"+fieldValue+"'");
			}
			columnName = entityName + '.' + fieldName;
		}

		if (field.getType().equals(String.class) == false) {
			return nonStringEqualsExp(entityClass, fieldName, fieldValue);
		}

		Object validatedValue = validateFieldValue(field, fieldValue);
		return Restrictions.naturalId().set(columnName, validatedValue);
	}

	private Criterion nonStringEqualsExp(Class<?> entityClass, String fieldName, Object value) {

		Criterion sqlRestriction = Restrictions.sqlRestriction("{alias}." + fieldName + " = '" + value + "'");

		return sqlRestriction;
	}

	private Object validateFieldValue(Field field, Object fieldValue) {
		if (null == fieldValue) {
			return 0;
		}

		Class<?> fieldType = field.getType();
		if (EntityUtil.isNumericField(field)) {
			if (fieldType.equals(Long.class)) {
				long value = Long.parseLong(fieldValue.toString());

				return value;
			}
		}
		return fieldValue;
	}

	/**
	 * 
	 * @param aliasName must match fieldName of entityClass
	 */
	private void setCurrentAlias(String aliasName) {
		if (null == aliasName || aliases.get(aliasName)!=null)
			return;

		if (aliasName.equals("this")) {
			//this.currentAlias = "this_";
		} else {
			
			Field correspondingField = EntityUtil.getDeclaredField(entityClass, aliasName);
			if(null == correspondingField) {
				return;
			}
			aliases.put(aliasName, joinIndex);
			criteria.createAlias(entityClass.getSimpleName() + "." + aliasName, aliasName);
			if (aliasName.length() > 10) {
				aliasName = aliasName.substring(0, 10);
			}

			//this.currentAlias = aliasName.toLowerCase() + joinIndex + '_';
			
			joinIndex++;
		}
	}

	
	private String getAlias(String aliasName) {
		if("this".equals(aliasName)) {
			return "this_";
		}
		return aliasName.toLowerCase()+aliases.get(aliasName)+"_";
	}
	
	private void setJoinColumnAliases( ) {
		List<Field> joinColumns = QueryUtil.getJoinColumnFields(entityClass);
		for (int i = 0; i < joinColumns.size(); i++) {
			setCurrentAlias(joinColumns.get(i).getName());
		}
		 
	}
	private Criteria criteria;
	public Criteria createCriteria() {
		Map<String, Object> fieldsFilter = filter.getFieldsFilter();
		List<Field> entityDeclaredFields = EntityUtil.getDeclaredFields(entityClass);

		log.info("=======FILTER: {}", fieldsFilter); 

		String entityName = entityClass.getSimpleName();
		criteria = hibernateSession.createCriteria(entityClass, entityName);
		setJoinColumnAliases();
		setCurrentAlias("this"); 

		for (final String rawKey : fieldsFilter.keySet()) {
			setCurrentAlias("this");

			log.info("##" + rawKey + ":" + fieldsFilter.get(rawKey));

			if (fieldsFilter.get(rawKey) == null)
				continue;

			String currentKey = rawKey;
			boolean itemExacts = allItemExactSearch;
			String finalNameAfterExactChecking = currentItemExact(rawKey);

			if (null != finalNameAfterExactChecking) {
				
				currentKey = finalNameAfterExactChecking;
				itemExacts = true;
				boolean multiKey = currentKey.contains(",");
				
				if(multiKey) {
					setCurrentAlias(currentKey.split(",")[0]);
				}
				
				criteria.add(restrictionEquals(entityClass, currentKey, fieldsFilter.get(rawKey)));
				continue;
			}

			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey);

			// check if date
			Criterion dateFilterSql = getDateFilter(rawKey, currentKey, entityDeclaredFields, fieldsFilter);

			if (null != dateFilterSql) {
				log.info(" {} is date ", rawKey);
				criteria.add(dateFilterSql);
				continue;
			}

			boolean multiKey = rawKey.contains(",");

			Field field;
			if (multiKey) {
				Field hisField = EntityUtil.getDeclaredField(entityClass, rawKey.split(",")[0]);
				field = EntityUtil.getDeclaredField(hisField.getType(), rawKey.split(",")[1]);
			} else {
				field = QueryUtil.getFieldByName(currentKey, entityDeclaredFields);
			}

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				continue;
			}

			String fieldName = field.getName();
			KeyValue joinColumnResult = QueryUtil.checkIfJoinColumn(currentKey, field, false);

			if (null != joinColumnResult) {
				if (joinColumnResult.isValid()) {
 
					
					criteria.add(restrictionLike(fieldName + "." + joinColumnResult.getValue(), field.getType(),
							fieldsFilter.get(rawKey)));
				} else {
					continue;
				}
			} else {
				if (itemExacts) {
					criteria.add(restrictionEquals(entityClass, currentKey, fieldsFilter.get(rawKey)));
				} else {
					criteria.add(restrictionLike(entityName + "." + currentKey, entityClass, fieldsFilter.get(rawKey)));
				}
			}

		}

		try {
			addOrderOffsetLimit(criteria, filter);
		} catch (Exception e) {
			log.error("Error adding order/offset/limit");
			e.printStackTrace();
		}

		return criteria;

	}

	public Criteria createRowCountCriteria() { 

		Criteria criteria = createCriteria();
		criteria.setProjection(Projections.rowCount());
		return criteria;
	}

	private void addOrderOffsetLimit(Criteria criteria, Filter filter) {
		if (filter.getLimit() > 0) {
			criteria.setMaxResults(filter.getLimit());
			if (filter.getPage() > 0) {
				criteria.setFirstResult(filter.getPage() * filter.getLimit());
			}
		}

		if (null != filter.getOrderBy()) {
			Order order;

			if (filter.getOrderType().toLowerCase().equals("desc")) {
				order = Order.desc(filter.getOrderBy());
			} else {
				order = Order.asc(filter.getOrderBy());
			}

			criteria.addOrder(order);
		}

	}

	private Criterion restrictionLike(final String fieldName, Class<?> _class, Object value) {
		String extractedFieldName = fieldName;
		if (fieldName.contains(".") && fieldName.split("\\.").length == 2) {
			extractedFieldName = fieldName.split("\\.")[1];
		}
		Field field = EntityUtil.getDeclaredField(_class, extractedFieldName);
		boolean stringTypeField = field.getType().equals(String.class);
		Object validatedValue = validateFieldValue(field, value);

		if (!stringTypeField) {

			return nonStringLikeExp(field, _class, validatedValue);
		}

		SimpleExpression likeExp = Restrictions.like(fieldName, String.valueOf(validatedValue), MatchMode.ANYWHERE);

		return likeExp;

	}

	private Criterion nonStringLikeExp(Field field, Class<?> _class, Object value) {

		String columnName = field.getName();// QueryUtil.getColumnName(field);
		String tableName = _class.getName();// QueryUtil.getTableName(_class); NOW USING ALIAS

		boolean	isJoinColumn = field.getAnnotation(JoinColumn.class)!=null;		
		String alias = isJoinColumn ? getAlias(field.getName()) : getAlias("this");
		Criterion sqlRestriction = Restrictions
				.sqlRestriction(alias  + "." + columnName + " LIKE '%" + value + "%'");

		return sqlRestriction;
	}

	

	private Criterion getDateFilter(String rawKey, String key, List<Field> entityDeclaredFields,
			Map<String, Object> filter) {
		boolean dayFilter = rawKey.endsWith(QueryUtil.DAY_SUFFIX);
		boolean monthFilter = rawKey.endsWith(QueryUtil.MONTH_SUFFIX);
		boolean yearFilter = rawKey.endsWith(QueryUtil.YEAR_SUFFIX);

		if (dayFilter || monthFilter || yearFilter) {

			String fieldName = key;
			String mode = QueryUtil.FILTER_DATE_DAY;

			if (dayFilter) {
				fieldName = key.replace(QueryUtil.DAY_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_DAY;

			} else if (monthFilter) {
				fieldName = key.replace(QueryUtil.MONTH_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_MON1TH;

			} else if (yearFilter) {
				fieldName = key.replace(QueryUtil.YEAR_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_YEAR;

			}
			Field field = EntityUtil.getObjectFromListByFieldName("name", fieldName, entityDeclaredFields);
			Object value = filter.get(key);
			String columnName = QueryUtil.getColumnName(field);
			log.info("mode: {}. value: {}", mode, value);
			Criterion restriction = Restrictions.sqlRestriction(mode + "(" + columnName + ")=" + value);

			return restriction;
		}

		return null;
	}

	private String currentItemExact(String rawKey) {
		if (rawKey.endsWith("[EXACTS]")) {
			String finalKey = rawKey.split("\\[EXACTS\\]")[0];
			log.info("{} exact search", finalKey);
			return finalKey;
		}
		return null;
	}

}
