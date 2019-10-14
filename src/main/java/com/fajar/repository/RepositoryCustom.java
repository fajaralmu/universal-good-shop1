package com.fajar.repository;

import java.util.List;


public interface RepositoryCustom<T> {

	List<T> filterAndSort(String q,  Class clazz, String entityGraph);
	
	List<T> filterAndSort(String q, Class clazz);
	
	int countFilterAndSort(String q);
	
	List<T> toDesiredObject(List<Object> l_o);
}
