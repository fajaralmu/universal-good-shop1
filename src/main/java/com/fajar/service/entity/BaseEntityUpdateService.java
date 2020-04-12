package com.fajar.service.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.service.FileService;
import com.fajar.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;
 
@Service
@Slf4j
public   class BaseEntityUpdateService {
	
	@Autowired
	protected FileService fileService;
	
	
	public ShopApiResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		
		return ShopApiResponse.failed("method not implemented");
	} 
	
	protected Object copyNewElement(Object source, boolean newRecord) {
		return EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);
	}

	protected List removeNullItemFromArray(String[] array) {
		List<Object> result = new ArrayList<>();
		for (String string : array) {
			if (string != null) {
				result.add(string);
			}
		}
		return result;

	}
}
