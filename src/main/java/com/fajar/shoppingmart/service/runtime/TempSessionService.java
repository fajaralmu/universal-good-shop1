package com.fajar.shoppingmart.service.runtime;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class TempSessionService   {
 
	private final FlatFileAccessor flatFileAccessor;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public TempSessionService(FlatFileAccessor fileAccessor) {
		this.flatFileAccessor = fileAccessor;
		objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	 
	}
	
	public <T> T get(String key, Class<T> _class) throws Exception {
		 
		String json = flatFileAccessor.getLineContent(key);
		log.debug("TempSessionService get JSON: {}", json);
		if(null == json) {
			return null;
		}
		return objectMapper.readValue(json, _class);
	}

	public void put(String key, Serializable registryModel) throws  Exception {
		 
		String json = objectMapper.writeValueAsString(registryModel);
		flatFileAccessor.putKeyValue(key, json);
	}

	public void remove(String key) throws Exception {
		 
		flatFileAccessor.removeLineWithKey(key);
	}
	
	 
}
