package com.fajar.repository;

import java.util.List;

import javax.persistence.Query;

 

public interface RepositoryCustom<T> {

	List<T> filterAndSort(String q,  Class clazz, String entityGraph);
	
	List<T> filterAndSort(String q, Class clazz);
	
	Object getSingleResult(String q); 
	
	Query createNativeQuery(String sql);

	public Object getCustomedObjectFromNativeQuery(String sql, Class<?> objectClass);
}
