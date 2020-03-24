package com.fajar.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.repository.EntityRepository;

@Service
public class CommonUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	
	@Override
	public ShopApiResponse saveEntity(BaseEntity entity, boolean newRecord) {
		entity = (BaseEntity) copyNewElement(entity, newRecord);
		BaseEntity newEntity = entityRepository.save(entity);
		return ShopApiResponse.builder().entity(newEntity).build();
	}
}
