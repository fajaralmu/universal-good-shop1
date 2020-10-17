package com.fajar.shoppingmart.service.transaction;

import java.util.List;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;

public interface TransactionHistoryService {

	public WebResponse getCashFlow(WebRequest request);

	public WebResponse getCashflowDetail(WebRequest request, String requestId);

	public WebResponse getCashflowMonthly(WebRequest request, String requestId);

	public WebResponse getCashflowDaily(WebRequest request, String requestId);

	public WebResponse getBalance(WebRequest request);
	

	public List<Supplier> getProductSupplier(Long id, int limit, int offset);

	public Transaction getFirstTransaction(Long id);

	public int getMinTransactionYear();

	public WebResponse getTransactionData(String transactionCode);
}
