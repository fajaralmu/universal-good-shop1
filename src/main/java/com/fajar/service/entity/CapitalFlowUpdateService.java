package com.fajar.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.WebResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.CapitalFlow;
import com.fajar.repository.EntityRepository;
import com.fajar.service.CashBalanceService;

@Service
public class CapitalFlowUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord ,EntityUpdateInterceptor entityUpdateInterceptor) {
		CapitalFlow capital = (CapitalFlow) copyNewElement(entity, newRecord);
		
//		if(newRecord) {
//			return ShopApiResponse.failed("Unable to update!");
//		}
		 
		BaseEntity newEntity = entityRepository.save(capital); 
		cashBalanceService.updateCashBalance(newEntity);
		
		return WebResponse.builder().entity(newEntity).build();
	}
}
