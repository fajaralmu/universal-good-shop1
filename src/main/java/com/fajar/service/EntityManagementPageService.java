package com.fajar.service;

import static com.fajar.util.MvcUtil.constructCommonModel;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fajar.entity.setting.EntityManagementConfig;
import com.fajar.entity.setting.EntityProperty;
import com.fajar.repository.EntityRepository;
import com.fajar.util.EntityUtil;

@Service
public class EntityManagementPageService {
	
	@Autowired
	private EntityRepository entityRepository;
	
	public Model setModel(HttpServletRequest request, Model model, String key) {
		
		EntityManagementConfig entityConfig = entityRepository.getConfig(key);
		
		if(null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key!");
		}
		
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityConfig.getEntityClass(), null); 
		model = constructCommonModel(request, entityProperty, model, entityConfig.getClass().getSimpleName(), "management");
		
		return model;
	}

}
