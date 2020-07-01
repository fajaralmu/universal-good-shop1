package com.fajar.shoppingmart.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.SystemException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;

import com.fajar.shoppingmart.annotation.CustomEntity;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.querybuilder.CriteriaBuilder;
import com.fajar.shoppingmart.querybuilder.QueryHolder;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Service
public class RepositoryCustomImpl implements RepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private Session hibernateSession; 

	public RepositoryCustomImpl() {
	}

	public void testHibernateSession(Class<? extends BaseEntity> entityClass)
			throws IllegalStateException, SystemException {
		Transaction tx = null;
		try {

			hibernateSession = sessionFactory.openSession();

			tx = hibernateSession.beginTransaction();

			Criteria criteria = hibernateSession.createCriteria(entityClass, entityClass.getSimpleName())
			// .createAlias("transactionHistory.myCashflowCategory", "myCashflowCategory")
//					.add(Restrictions.naturalId()
//							//.set("cifNumber", cif)							
//							.set("myCashflowCategory.module", module)
////							.set("sessionIdentifier", UUID.randomUUID().toString())
//					)
//					.addOrder(orderDate);
			;
			Order orderDate = Order.desc("date");
//				criteria.setMaxResults(limit);
//				criteria.setFirstResult(offset);

			List resultList = criteria.list();

			tx.commit();

		} catch (Exception e) {
			if (tx != null)
				tx.rollback();

			log.error("Error fetching from DB: {}", e);
			return;

		} finally {

			if (hibernateSession.isOpen())
				hibernateSession.close();
		}
	}

	@Override
	public <T> List<T> filterAndSort(String sql, Class<? extends T> clazz) {

		log.info("==============GET LIST FROM NATIVE SQL: " + sql);
		List<T> resultList = entityManager.createNativeQuery(sql, clazz).getResultList();
		if (resultList == null) {
			resultList = new ArrayList<>();
		}
		log.info("==============SQL OK: {}", resultList.size());
		return resultList;

	}

	@Override
	public Object getSingleResult(String sql) {
		log.info("=============GETTING SINGLE RESULT SQL: {}", sql);
		Object result = entityManager.createNativeQuery(sql).getSingleResult();
		log.info("=============RESULT SQL: {}, type: {}", result,
				result != null ? result.getClass().getCanonicalName() : null);
		return result;
	}

	@Override
	public Query createNativeQuery(String sql) {
		Query q = entityManager.createNativeQuery(sql);
		return q;
	}

	@Override
	public <O> O getCustomedObjectFromNativeQuery(String sql, Class<O> objectClass) {
		log.info("SQL for result object: {}", sql);
		try {
			O singleObject = objectClass.getDeclaredConstructor().newInstance();

			Query result = entityManager.createNativeQuery(sql);
			Object resultObject = result.getSingleResult();
			log.info("object ,{}", resultObject);
			/**
			 * check if object has custom entity annotation
			 */
			if (null == resultObject || objectClass.getAnnotation(CustomEntity.class) == null) {
				return null;
			}

			/**
			 * mapping result list to object fields based on information from the
			 * CustomEntity annotation
			 */
			CustomEntity customEntitySetting = (CustomEntity) objectClass.getAnnotation(CustomEntity.class);
			Object[] propertiesArray = (Object[]) resultObject;
			String[] propertyOrder = customEntitySetting.propOrder();

			singleObject = fillObject(singleObject, propertiesArray, propertyOrder);
			log.info("RESULT OBJECT: {}", singleObject);
			return singleObject;
		} catch (Exception e) {
			log.error("ERROR GET RECORD: " + e.getMessage());
			return null;
		}

	}

	private <O> O fillObject(O object, Object[] propertiesArray, String[] propertyOrder)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		for (int j = 0; j < propertiesArray.length; j++) {
			String propertyName = propertyOrder[j];
			Object propertyValue = propertiesArray[j];
			Field field = EntityUtil.getDeclaredField(object.getClass(), propertyName);
//					object.getClass().getDeclaredField(propertyName);
//			field.setAccessible(true);

			if (field != null && propertyValue != null) {
				final Class<?> fieldType = field.getType();
				if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
					log.info("type integer ==========================> : {}", propertyValue);
					propertyValue = Integer.parseInt(propertyValue.toString());
				} else if (field.getType().equals(Long.class) || fieldType.equals(long.class)) {
					log.info("type long ==========================> : {}", propertyValue);
					propertyValue = Long.parseLong(propertyValue.toString());
				} else if (field.getType().equals(Double.class) || fieldType.equals(double.class)) {
					log.info("type double ==========================> : {}", propertyValue);
					propertyValue = Double.parseDouble(propertyValue.toString());
				}

				field.set(object, propertyValue);
			}

		}
		return object;
	}

	@Override
	public <T> List<T> filterAndSort(QueryHolder queryHolder, Class<? extends T> objectClass) {

		return filterAndSort(queryHolder.getSqlSelect(), objectClass);
	}

	@Override
	public Object getSingleResult(QueryHolder queryHolder) {

		return getSingleResult(queryHolder.getSqlSingleResult());
	}

	@Override
	public <T> List<T> filterAndSortv2(Class<T> _class, Filter filter) {
		try {
			CriteriaBuilder criteriaBuilder = new CriteriaBuilder(hibernateSession);
			Criteria criteria = criteriaBuilder.createCriteria(_class, filter, false);
			List<T> resultList = criteria.list();

			if (null == resultList) {
				resultList = new ArrayList<>();
			}

			log.info("resultList length: {}", resultList.size());
			return resultList;
		} catch (Exception e) {
			log.error("Error filter and sort v2: {}", e);
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public long getRowCount(Class<?> _class, Filter filter) {

		try {
			CriteriaBuilder criteriaBuilder = new CriteriaBuilder(hibernateSession);
			Criteria criteria = criteriaBuilder.createRowCountCriteria(_class, filter);
			return (long) criteria.uniqueResult();
		} catch (Exception e) {
			return 0;
		}
	}

}
