package com.fajar.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RepositoryCustomImpl<T> implements RepositoryCustom<T> {

	@PersistenceContext
	EntityManager em;

	@Override
	public List<T> filterAndSort(String q, Class clazz, String entityGraph) {
		log.info("native query: " + q);
		 List<T> ls = em.createNativeQuery(q, clazz).getResultList();
		return ls;

	}

	@Override
	public List<T> filterAndSort(String q, Class clazz) {
		return filterAndSort(q, clazz, null);

	}

	@Override
	public int countFilterAndSort(String q) {
		BigInteger count = (BigInteger) em.createNativeQuery(q).getSingleResult();
		// System.out.println("COUNT : "+count);
		return count.intValue();
	}

	@Override
	public List<T> toDesiredObject(List<Object> l_o) {
//		List<T> l_t = new ArrayList<>();
//		for(Object o:l_o) {
//			l_t.add((T) o);
//		}
		return null;
	}

}
