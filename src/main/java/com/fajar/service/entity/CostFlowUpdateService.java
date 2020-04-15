package com.fajar.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.CostFlow;
import com.fajar.repository.EntityRepository;
import com.fajar.service.CashBalanceService;

@Service
public class CostFlowUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	
	@Override
	public ShopApiResponse saveEntity(BaseEntity entity, boolean newRecord) {
		CostFlow costFlow = (CostFlow) copyNewElement(entity, newRecord); 
		
		if(newRecord) {
			return ShopApiResponse.failed("Unable to update");
		}
		BaseEntity newEntity = entityRepository.save(costFlow);
		cashBalanceService.update(newEntity);
		
		return ShopApiResponse.builder().entity(newEntity).build();
	}
}
