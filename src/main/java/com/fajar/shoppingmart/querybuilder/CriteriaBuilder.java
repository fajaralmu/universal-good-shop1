package com.fajar.shoppingmart.querybuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;

import com.fajar.shoppingmart.dto.KeyValue;
import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.Unit;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CriteriaBuilder {

	static SessionFactory factory;
	static Session session;
	static {
		int i = 0;
		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

		configuration.setProperties(additionalProperties());
		configuration.addAnnotatedClass(Product.class);
		configuration.addAnnotatedClass(Unit.class);
		configuration.addAnnotatedClass(Category.class);
//		addAnnotatedClass(configuration);

		factory = configuration./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory();
		session = factory.openSession();
	}

	private static Properties additionalProperties() {

		String dialect = "org.hibernate.dialect.MySQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/goodshop");
		properties.setProperty("hibernate.connection.username","root");
		properties.setProperty("hibernate.connection.password", "");

		properties.setProperty("hibernate.connection.driver_class", com.mysql.jdbc.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);
		return properties;
	}

	public static Criteria createWhereClause(Class<?> entityClass, Map<String, Object> filter,
			final boolean allItemExactSearch) {

		String tableName = QueryUtil.getTableName(entityClass);
		List<QueryFilterItem> sqlFilters = new ArrayList<QueryFilterItem>();
		List<Field> entityDeclaredFields = EntityUtil.getDeclaredFields(entityClass);

		log.info("=======FILTER: {}", filter);

//		filter.put(TABLE_NAME, tableName);
		String entityName = entityClass.getSimpleName();
		Criteria criteria = session.createCriteria(entityClass, entityName);

		for (final String rawKey : filter.keySet()) {
			log.info("................." + rawKey + ":" + filter.get(rawKey));

			if (filter.get(rawKey) == null)
				continue;

			String currentKey = rawKey;
			boolean itemExacts = allItemExactSearch;
			String filterTableName = tableName;
			String finalNameAfterExactChecking = currentItemExact(rawKey);

			if (null != finalNameAfterExactChecking) {
				currentKey = finalNameAfterExactChecking;
				itemExacts = true;
				criteria.add(Restrictions.naturalId().set(currentKey, filter.get(rawKey)));
			}  

			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey);

			QueryFilterItem queryItem = new QueryFilterItem();
			queryItem.setExacts(itemExacts);

			// check if date
			Criterion dateFilterSql = getDateFilter(rawKey, currentKey, entityDeclaredFields, filter);

			if (null != dateFilterSql) {

				continue;
			}

			Field field = QueryUtil.getFieldByName(currentKey, entityDeclaredFields);

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				continue;
			}

			String filterColumnName = QueryUtil.getColumnName(field);
			String fieldName = field.getName();
			KeyValue joinColumnResult = QueryUtil.checkIfJoinColumn(currentKey, field);

			if (null != joinColumnResult) {
				if (joinColumnResult.isValid()) {

					criteria.createAlias(entityName + "." + fieldName, fieldName);
					criteria.add(Restrictions.like(fieldName + "." + joinColumnResult.getValue(),
							"%"+filter.get(rawKey)+"%"));
//					filterTableName = joinColumnResult.getKey().toString();
//					filterColumnName = joinColumnResult.getValue().toString();
				} else {
					continue;
				}
			}else {
				criteria.add(Restrictions.like(currentKey, "%"+filter.get(rawKey)+"%"));
			}

//			queryItem.setTableName(filterTableName);
//			queryItem.setColumnName(filterColumnName);
//			queryItem.setValue(filter.get(rawKey));
//			sqlFilters.add(queryItem);
		}

		return criteria;

	}

	public static void main(String[] args) {
		Map<String, Object> filter = new HashMap<String, Object>() {
			{
				put("unit", "");
			}
		};
		Criteria criteria = createWhereClause(Product.class, filter, false);
		System.out.println("CRITERIA BUILT");
		List list = criteria.list();
		CollectionUtil.printArray(list.toArray());
		System.out.println(criteria.getClass());
	}

	private static Criterion getDateFilter(String rawKey, String key, List<Field> entityDeclaredFields,
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
			Object value = filter.get(key);

			Criterion restriction = Restrictions.sqlRestriction(mode + "(" + fieldName + ")=", value,
					IntegerType.INSTANCE);
			return restriction;
		}

		return null;
	}

	private static String currentItemExact(String rawKey) {
		if (rawKey.endsWith("[EXACTS]")) {
			String finalKey = rawKey.split("\\[EXACTS\\]")[0];
			log.info("{} exact search", finalKey);
			return finalKey;
		}
		return null;
	}

}
