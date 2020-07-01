package com.fajar.shoppingmart.querybuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.KeyValue;
import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.Unit;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.UserRole;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CriteriaBuilderService {
 
	
	@Autowired
	private Session hibernateSession;
	
	static {

		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

		configuration.setProperties(additionalProperties());
		configuration.addAnnotatedClass(Product.class);
		configuration.addAnnotatedClass(Unit.class);
		configuration.addAnnotatedClass(Category.class);
		configuration.addAnnotatedClass(Transaction.class);
		configuration.addAnnotatedClass(Supplier.class);
		configuration.addAnnotatedClass(Customer.class);
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(UserRole.class);
//		addAnnotatedClass(configuration);

//		factory = configuration./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory();
//		session = factory.openSession();
	}

	private static Properties additionalProperties() {

		String dialect = "org.hibernate.dialect.MySQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/goodshop");
		properties.setProperty("hibernate.connection.username", "root");
		properties.setProperty("hibernate.connection.password", "");

		properties.setProperty("hibernate.connection.driver_class", com.mysql.jdbc.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);
		return properties;
	}

	public Criteria createCriteria(Class<?> entityClass, Filter filter, final boolean allItemExactSearch) {
		Map<String, Object> fieldsFilter = filter.getFieldsFilter();
		List<Field> entityDeclaredFields = EntityUtil.getDeclaredFields(entityClass);

		log.info("=======FILTER: {}", fieldsFilter);

		String entityName = entityClass.getSimpleName();
		Criteria criteria = hibernateSession.createCriteria(entityClass, entityName);

		for (final String rawKey : fieldsFilter.keySet()) {
			log.info("##" + rawKey + ":" + fieldsFilter.get(rawKey));

			if (fieldsFilter.get(rawKey) == null)
				continue;

			String currentKey = rawKey;
			boolean itemExacts = allItemExactSearch;
			String finalNameAfterExactChecking = currentItemExact(rawKey);

			if (null != finalNameAfterExactChecking) {
				currentKey = finalNameAfterExactChecking;
				itemExacts = true;
				criteria.add(Restrictions.naturalId().set(currentKey, fieldsFilter.get(rawKey)));
			}

			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey);

			QueryFilterItem queryItem = new QueryFilterItem();
			queryItem.setExacts(itemExacts);

			// check if date
			Criterion dateFilterSql = getDateFilter(rawKey, currentKey, entityDeclaredFields, fieldsFilter);

			if (null != dateFilterSql) {
				log.info(" {} is date ", rawKey);
				criteria.add(dateFilterSql);
				continue;
			}

			Field field = QueryUtil.getFieldByName(currentKey, entityDeclaredFields);

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				continue;
			}

			String fieldName = field.getName();
			KeyValue joinColumnResult = QueryUtil.checkIfJoinColumn(currentKey, field, true);

			if (null != joinColumnResult) {
				if (joinColumnResult.isValid()) {

					criteria.createAlias(entityName + "." + fieldName, fieldName);
					criteria.add(
							restrictionsLike(fieldName + "." + joinColumnResult.getValue(), fieldsFilter.get(rawKey)));
				} else {
					continue;
				}
			} else {
				criteria.add(restrictionsLike(currentKey, fieldsFilter.get(rawKey)));
			}

		}

		try {
			addOrderOffsetLimit(criteria, filter);
		}catch (Exception e) {
			log.error("Error adding order/offset/limit");
			e.printStackTrace();
		}

		return criteria;

	}

	private static void addOrderOffsetLimit(Criteria criteria, Filter filter) {
		if (filter.getLimit() > 0) {
			criteria.setMaxResults(filter.getLimit());
		}
		if (filter.getPage() > 0) {
			criteria.setFirstResult(filter.getPage());
		}
		if (null != filter.getOrderBy()) {
			Order order;
			
			if (filter.getOrderType().toLowerCase().equals("desc")) { 
				order = Order.desc(filter.getOrderBy());
			}else {
				order = Order.asc(filter.getOrderBy());
			}

			criteria.addOrder(order);
		}

	}

	static SimpleExpression restrictionsLike(String fieldName, Object value) {
		return Restrictions.like(fieldName, likeExp(value));
	}

	static String likeExp(Object value) {
		return "%" + value + "%";
	}

	public static void main(String[] args) {
//		Map<String, Object> filter = new HashMap<String, Object>() {
//			{
//				 
//				put("transactionDate-month", 2);
//			}
//		};
//		Criteria criteria = createWhereClause(Transaction.class, filter, false);
//		criteria.setMaxResults(2);
//		System.out.println("CRITERIA BUILT");
//		List list = criteria.list();
//		CollectionUtil.printArray(list.toArray());
//		System.out.println(criteria.getClass());
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
			Field field = EntityUtil.getObjectFromListByFieldName("name", fieldName, entityDeclaredFields);
			Object value = filter.get(key);
			String columnName = QueryUtil.getColumnName(field);
			log.info("mode: {}. value: {}", mode, value);
			Criterion restriction = Restrictions.sqlRestriction(mode + "(" + columnName + ")=" + value);

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
