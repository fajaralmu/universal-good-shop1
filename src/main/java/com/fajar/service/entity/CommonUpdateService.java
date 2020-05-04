package com.fajar.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.WebResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.repository.EntityRepository;

@Service
public class CommonUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord) {
		entity = (BaseEntity) copyNewElement(entity, newRecord);
		BaseEntity newEntity = entityRepository.save(entity);
		return WebResponse.builder().entity(newEntity).build();
	}
}
