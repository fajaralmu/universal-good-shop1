package com.fajar.shoppingmart.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fajar.shoppingmart.dto.KeyValue;
import com.fajar.shoppingmart.entity.BaseEntity;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CollectionUtil {
	public static <T> List<T> arrayToList(T[] array) {
		List<T> list = new ArrayList<T>();
		for (T t : array) {
			list.add(t);
		}
		 
		return list;

	}
	
	public static <T> T[] listToArray(List<T> list) {  
		
		T el = list.get(0);
		Class<?> _class = el.getClass();
		log.info("List to array of: {}", _class);
		
		T[] array = (T[]) Array.newInstance(_class, list.size());
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array ;
	}
	
	public static <T> List<T> listOf(T... elements){
		
		List<T> list = new ArrayList<>();
		for (int i = 0; i < elements.length; i++) {
			list.add(elements[i]);
		}
		return list ;
	}

	public static void main(String[] args) {
		List<Object> list = listOf("a", "1", "2");
		System.out.println(listToArray(list));
	}

	public static List< BaseEntity> mapToList(Map<? extends Object, ? extends BaseEntity> map) {
		List< BaseEntity> list = new ArrayList<>();
		for (Object key : map.keySet()) {
			list.add(map.get(key));
		}

		return list;
	}
 

	public static <A, B> List<A> convertList(List<B> list) {
		List<A> newList = new ArrayList<A>();
		try {
			for (B object : list) { 
				newList.add((A) object); 
			}
		}catch (Exception e) {
			 log.error("Error convert List");
		}
		return newList;
	}
	
	 

	public static String[] toArrayOfString(List validUrls) {
		if(validUrls == null) {
			return new String[] {};
		}
		String[] array = new String[validUrls.size()];
		for (int i = 0; i < validUrls.size(); i++) {
			array[i] = validUrls.get(i).toString();
		}
		return array;
	}
	
	public static List<KeyValue> yearArray(int min, int max){
		List<KeyValue> years = new ArrayList<>();
		for(int i = min; i <= max; i++) {
			years.add(new KeyValue(i, i, true));
		}
		
		return years ;
	}
	
	

}
