package com.fajar.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RepositoryCustomImpl<T> implements RepositoryCustom<T> {

	@PersistenceContext
	EntityManager em;

	@Override
	public List<T> filterAndSort(String sql, Class clazz, String entityGraph) {
		log.info("==============GET LIST FROM NATIVE SQL: " + sql);
		List<T> resultList = em.createNativeQuery(sql, clazz).getResultList();
		if(resultList == null) {
			resultList = new ArrayList<>();
		}
		log.info("==============SQL OK: {}",resultList.size());
		return resultList;

	}

	@Override
	public List<T> filterAndSort(String q, Class clazz) {
		return filterAndSort(q, clazz, null);

	}

	@Override
	public Object getSingleResult(String sql) {
		log.info("=============GETTING SINGLE RESULT SQL: {}",sql);
		Object result =  em.createNativeQuery(sql).getSingleResult();
		log.info("=============RESULT SQL: {}, type: {}",result, result != null? result.getClass().getCanonicalName():null);
		return result;
	}

	@Override
	public Query createNativeQuery(String sql) {
		Query q = em.createNativeQuery(sql);
		return q;
	}

}
