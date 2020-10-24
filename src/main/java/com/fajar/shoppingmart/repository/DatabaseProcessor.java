package com.fajar.shoppingmart.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.querybuilder.CriteriaBuilder;
import com.fajar.shoppingmart.querybuilder.QueryUtil;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseProcessor {
 
	private Transaction currentTransaction;
	private final Session hibernateSession;
	boolean removeTransactionAfterPersistence = true;
	
	public DatabaseProcessor(Session sess) {  
		this.hibernateSession = sess;
	}
	
	public boolean isTransactionNotKept() {
		return removeTransactionAfterPersistence;
	}

	public void keepTransaction() {
		this.removeTransactionAfterPersistence = false;
	}

	public void notKeepingTransaction() {
		this.removeTransactionAfterPersistence = true;
	}
	
	public static <T extends BaseEntity> void saveNewRecord(T entity, Session hibernateSession) {
		
		Long id = (Long) hibernateSession.save(entity);
		entity.setId(id);
	}
	public long getRowCount(Class<? extends BaseEntity> _class, Filter filter) {
		
		try {
			CriteriaBuilder criteriaBuilder = new CriteriaBuilder(hibernateSession, _class, filter);
			Criteria criteria = criteriaBuilder.createRowCountCriteria();
			
			return (long) criteria.uniqueResult();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public <T> T getById(Class<T> _class, Serializable id){
		T object = (T) hibernateSession.get(_class, id);
		refresh();
		return object;
	}
	
	public void close() {
		
		try {
			if(null!=hibernateSession) {
			//	hibernateSession.close();
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	public <T> List<T> findByKeyAndValues(Class<T> entityClass, String key, Object... values) {
		
		if(values == null ) {
			log.error("break findByKeyAndValues >> VALUES IS NULL");
			return CollectionUtil.emptyList();
		}
		
		log.info("findByKeyAndValues, class: {}, key: {}, values.length: {}", entityClass, key, values.length);
		List<T> res = this.pesistOperation(new PersistenceOperation<List<T>>() {

			@Override
			public List<T> doPersist(Session hibernateSession) {
				Criteria criteria = hibernateSession.createCriteria(entityClass);
				Criterion[] predictates = new Criterion[values.length];
				for (int i = 0; i < values.length; i++) {
					predictates[i] = (Restrictions.naturalId().set(key, values[i]));
				}

				criteria.add(Restrictions.or(predictates));
				List list = criteria.list();
				 
				log.info("RESULT findByKeyAndValues:{}", list == null ? "NULL" : list.size());
				return list;
			}

		});

		return res;
	}
	public <T extends BaseEntity> List<T> filterAndSortv2(Class<T> _class, Filter filter) {
		try {
			CriteriaBuilder criteriaBuilder = new CriteriaBuilder(hibernateSession, _class, filter);
			Criteria criteria = criteriaBuilder.createCriteria();
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
		return CollectionUtil.emptyList();
	}
	
	public <T extends BaseEntity> T saveObject(final T rawEntity) {

		if (null == rawEntity) {
			log.error("rawEntity IS NULL");
			return null;
		}

		PersistenceOperation<T> persistenceOperation = new PersistenceOperation<T>() {

			@Override
			public T doPersist(Session hibernateSession) {
				T result, entity;
				try {
					entity = validateJoinColumns(rawEntity);
				} catch (Exception e) {
					entity = rawEntity;
					e.printStackTrace();
				}

				if (entity.getId() == null) {
					log.debug("Will save new entity ");
					rawEntity.setCreatedDate(new Date());

					Long newId = (Long) hibernateSession.save(entity);
					result = entity;
					result.setId(newId);

					log.debug("success add new record of {} with new ID: {}", entity.getClass(), newId);
				} else {
					log.debug("Will update entity :{}", entity.getId());
					entity.setModifiedDate(new Date());

					result = (T) hibernateSession.merge(entity);

					log.debug("success update record of {}  ", entity.getClass() );
				}

				log.info("success save Object of {} >> savedObject: {}", entity.getClass(), result);
				return result;
			}
		};

		T result = this.pesistOperation(persistenceOperation);
		if (null != result) {
			log.info("success save Object: {}", result.getClass());
		}

		return result;
	}
	
	public <T extends BaseEntity> T validateJoinColumns(T rawEntity) throws Exception {
		List<Field> joinColumnFields = QueryUtil.getJoinColumnFields(rawEntity.getClass());

		T entity = EntityUtil.cloneSerializable(rawEntity);

		if (0 == joinColumnFields.size()) {
			return entity;
		}
		for (Field field : joinColumnFields) {
			BaseEntity fieldValue = (BaseEntity) field.get(entity);
			// check from DB
			log.info("check join column field: {}->value: {}", field.getName(), fieldValue);
			if (null == fieldValue)
				continue;
			Object dbValue = hibernateSession.get(fieldValue.getClass(), fieldValue.getId());

			if (null == dbValue)
				continue;

			field.set(entity, dbValue);
		}

		return entity;

	}

	 
	public <T> T pesistOperation(PersistenceOperation<T> persistenceOperation) {

		try {
			if (null == persistenceOperation) {
				throw new Exception("persistenceOperation must not be NULL");
			}
			if (null == currentTransaction) {
				log.info("Hibernate begin new Transaction");
				currentTransaction = hibernateSession.beginTransaction();
			}

			T result = persistenceOperation.doPersist(hibernateSession);

			if (isTransactionNotKept()) {
				hibernateSession.flush();
				currentTransaction.commit();
				
				log.info("==**COMMITED Transaction**==");
			}else {
				log.info("Not committing transaction");
			}

			log.info("success persist operation commited: {}", removeTransactionAfterPersistence);
			return result;

		} catch (Exception e) {
			log.error("Error persist operation: {}", e);

			if (currentTransaction != null) {
				log.info("Rolling back.... ");
				currentTransaction.rollback();
			}
			notKeepingTransaction();

			e.printStackTrace();
		} finally {

			if (isTransactionNotKept()) {
				currentTransaction = null;
			}
			this.refresh();
		}
		return null;
	}
	
	public void refresh() {
		try {
		if (hibernateSession != null) {
			hibernateSession.clear(); // internal cache clear
		}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("ERROR refresh session: {}", e.getMessage());
		}
	}
	
	public boolean deleteObjectById(Class<? extends BaseEntity> _class, Long id) {
		PersistenceOperation<Boolean> deleteOperation = new PersistenceOperation<Boolean>() {

			@Override
			public Boolean doPersist(Session hibernateSession) {
				try {
					Object existingObject = hibernateSession.load(_class, id);
					if (null == existingObject) {
						log.info("existingObject of {} with id: {} does not exist!!", _class, id);
						return false;
					}
					hibernateSession.delete(existingObject);
					log.debug("Deleted Successfully");
					return true;
				} catch (Exception e) {
					log.error("Error deleting object!");
					e.printStackTrace();
					return false;
				}
			}
		};
		try {
			return this.pesistOperation(deleteOperation);
		} catch (Exception e) {
			return false;
		}
	}

}
