package com.fajar.shoppingmart.service.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.repository.InventoryItemRepository;
import com.fajar.shoppingmart.service.ReportingService;
import com.fajar.shoppingmart.service.financial.CashBalanceService;

@Service
public class TransactionHistoryServiceImpl implements TransactionHistoryService {
	
	@Autowired
	private ReportingService reportingService;
	@Autowired
	private CashBalanceService cashBalanceService;
	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	
	@Override
	public WebResponse getCashFlow(WebRequest request) {
		return reportingService.getCashFlow(request);
	}
	@Override
	public WebResponse getCashflowDetail(WebRequest request, String requestId) {
		WebResponse response = reportingService.getCashflowDetail(request, requestId);
		response.setFilter(request.getFilter());
		return response;
	}
	@Override
	public WebResponse getCashflowMonthly(WebRequest request, String requestId) {
		return reportingService.getCashflowMonthly(request, requestId);
	}
	@Override
	public WebResponse getCashflowDaily(WebRequest request, String requestId) {
		return reportingService.getCashflowDaily(request, requestId);
	}
	@Override
	public WebResponse getBalance(WebRequest request) {
		Filter filter = request.getFilter();
		CashBalance balance = cashBalanceService.getBalanceAt(filter.getDay(), filter.getMonth(), filter.getYear());
		return WebResponse.builder().entity(balance).build();
	}
	
	@Override
	public List<Supplier> getProductSupplier(Long id, int limit, int offset) {
		return reportingService.getProductSupplier(id, limit, offset);
	}
	@Override
	public Transaction getFirstTransaction(Long id) {
		return reportingService.getFirstTransaction(id);
	}
	@Override
	public int getMinTransactionYear() {
		return reportingService.getMinTransactionYear();
	}
	@Override
	public WebResponse getTransactionData(String transactionCode) {
		
		Transaction transaction = reportingService.getTransactionByCode(transactionCode);
		if(null == transaction) {
			return WebResponse.failed("Transaction Not Found");
		}
		WebResponse response = new WebResponse(); 
		response.setTransaction(transaction);
		response.setEntities(transaction.getProductFlows());
		return response;
	}
	@Override
	public WebResponse getAllInventoriesStock() {
		Integer count = inventoryItemRepository.getAllInventoriesStock();
		return WebResponse.builder().quantity(count).build();
	}

}
