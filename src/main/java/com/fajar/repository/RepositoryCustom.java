package com.fajar.repository;

import java.util.List;

import javax.persistence.Query;

import com.fajar.querybuilder.QueryHolder; 

public interface RepositoryCustom<T> {

	 
	List<T> filterAndSort(String q, Class<?> objectClass);
	
	List<T> filterAndSort(QueryHolder queryHolder, Class<?> objectClass);
	Object getSingleResult(QueryHolder queryHolder);
	Object getSingleResult(String q); 
	
	Query createNativeQuery(String sql);

	public Object getCustomedObjectFromNativeQuery(String sql, Class<?> objectClass);
}
