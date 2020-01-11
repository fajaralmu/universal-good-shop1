package com.fajar.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fajar.entity.BaseEntity;

public class CollectionUtil {
	public static <T> List<T> arrayToList(T[] array) {
		List<T> list = new ArrayList<T>();
		for (T t : array) {
			list.add(t);
		}
		return list;

	}

	public static void main(String[] args) {

	}

	public static List< BaseEntity> mapToList(Map<? extends Object, ? extends BaseEntity> map) {
		List list = new ArrayList();
		for (Object key : map.keySet()) {
			list.add(map.get(key));
		}

		return list;
	}

	public static <T> T[] listToArray(List<T> list) {
		int size = list.size();
		T[] array = (T[]) new Object[size];
		for (int i = 0; i < list.size(); i++) {
			T object = list.get(i);
			array[i] = object;

		}
		return array;

	}

	public static <T> List<T> convertList(List list) {
		List<T> newList = new ArrayList<T>();
		for (Object object : list) {
			newList.add((T) object);
		}
		return newList;
	}

}
