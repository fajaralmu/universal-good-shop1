package com.fajar.shoppingmart.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.CostFlow;
import com.fajar.shoppingmart.service.financial.CashBalanceService;

@Service
public class CostFlowUpdateService extends BaseEntityUpdateService<CostFlow>{ 
 
	@Autowired
	private CashBalanceService cashBalanceService;
	
	@Override
	public WebResponse saveEntity(CostFlow entity, boolean newRecord) {
		CostFlow costFlow =  copyNewElement(entity, newRecord); 
		
//		if(newRecord) {
//			return WebResponse.failed("Unable to update");
//		}
		CostFlow newEntity = entityRepository.save(costFlow);
		cashBalanceService.updateCashBalance(newEntity);
		
		return WebResponse.builder().entity(newEntity).build();
	}
}
