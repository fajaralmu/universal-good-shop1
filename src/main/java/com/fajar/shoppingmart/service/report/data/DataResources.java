package com.fajar.shoppingmart.service.report.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.repository.CapitalFlowRepository;
import com.fajar.shoppingmart.repository.CostFlowRepository;
import com.fajar.shoppingmart.repository.ProductFlowRepository;
import com.fajar.shoppingmart.service.financial.CashBalanceService;

import lombok.Data;
@Data
@Service
public class DataResources {

	@Autowired
	private CostFlowRepository costFlowRepository;
	@Autowired
	private CapitalFlowRepository capitalFlowRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	@Autowired
	private ProductFlowRepository productFlowRepository; 
}
