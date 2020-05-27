package com.fajar.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.WebResponse;
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
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord,EntityUpdateInterceptor entityUpdateInterceptor) {
		CostFlow costFlow = (CostFlow) copyNewElement(entity, newRecord); 
		
//		if(newRecord) {
//			return WebResponse.failed("Unable to update");
//		}
		CostFlow newEntity = entityRepository.save(costFlow);
		cashBalanceService.updateCashBalance(newEntity);
		
		return WebResponse.builder().entity(newEntity).build();
	}
}
