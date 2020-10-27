package com.fajar.shoppingmart.service.transaction;

import static com.fajar.shoppingmart.dto.TransactionType.PURCHASING;
import static com.fajar.shoppingmart.dto.TransactionType.SELLING;
import static com.fajar.shoppingmart.util.DateUtil.getDiffMonth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.annotation.CustomEntity;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.custom.CashFlow;
import com.fajar.shoppingmart.repository.CustomRepositoryImpl;
import com.fajar.shoppingmart.repository.InventoryItemRepository;
import com.fajar.shoppingmart.repository.ProductFlowRepository;
import com.fajar.shoppingmart.repository.TransactionRepository;
import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.financial.CashBalanceService;
import com.fajar.shoppingmart.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionHistoryServiceImpl implements TransactionHistoryService {
	
//	@Autowired
//	private ReportingService reportingService;
	@Autowired
	private CashBalanceService cashBalanceService;
	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository; 
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private ProgressService progressService;
	
	@Override
	public WebResponse getCashFlow(WebRequest request) {
		WebResponse response = new WebResponse();

		// getTransaction
		int month = request.getFilter().getMonth();
		int year = request.getFilter().getYear();
		String module = request.getFilter().getModule();
		
		TransactionType transactionType;
		switch (module) {
			case "IN":
			case "PURCHASING":
				transactionType = PURCHASING;
				break;
			case "OUT":
			case "SELLING":
			default:
				transactionType = SELLING;
				break;
		}
		CashFlow cashflow = getCashflowByPeriod(month, year, transactionType);

		if (cashflow != null) {
			cashflow.setModule(request.getFilter().getModule());
			response.setEntity(cashflow);
		}
		response.setTransactionYears(new int[] { getMinTransactionYear(), Calendar.getInstance().get(Calendar.YEAR) });
		return response;
	}
	
	@Override
	public WebResponse getCashflowDetail(WebRequest request, String requestId) {
		int monthFrom = request.getFilter().getMonth();
		int yearFrom = request.getFilter().getYear();
		int monthTo = request.getFilter().getMonthTo();
		int yearTo = request.getFilter().getYearTo();
		
		int diffMonth = getDiffMonth(monthFrom, yearFrom, monthTo, yearTo);
		int dayCount = DateUtil.getMonthDayCount(yearTo, monthTo);
		
		Calendar periodTo = Calendar.getInstance();
		periodTo.set(request.getFilter().getYearTo(), request.getFilter().getMonthTo() - 1, dayCount);

		List<int[]> periods = DateUtil.getMonths(periodTo, diffMonth);
		List<BaseEntity> purchasings = new ArrayList<>();
		List<BaseEntity> sellings = new ArrayList<>();
		Long maxValue = 0L;

		for (int[] period : periods) {
			System.out.println("o o PERIOD: "+period[0]+", "+period[1]);

			// supply
			CashFlow cashflowPurchasing = getCashflowByPeriod(period[1], period[0], PURCHASING);

			purchasings.add(cashflowPurchasing);

			if (cashflowPurchasing != null  && cashflowPurchasing.getAmount() > maxValue) {
				maxValue = cashflowPurchasing.getAmount();
			}

			// purchase
			CashFlow cashflowSelling = getCashflowByPeriod(period[1], period[0], SELLING);
			

			sellings.add(cashflowSelling);

			if (cashflowSelling != null  
					&& cashflowSelling.getAmount() > maxValue) {
				maxValue = cashflowSelling.getAmount();
			}
			progressService.sendProgress(1, periods.size(), 100, false, requestId);

		}
		WebResponse response = new WebResponse();

		response.setMaxValue(maxValue);
		//will be removed
		response.setSupplies(purchasings);
		response.setPurchases(sellings);
		//and replaced by this
		response.setPurchasings(purchasings);
		response.setSellings(sellings);
		response.setFilter(request.getFilter());
		return response;
	}
	@Override
	public WebResponse getCashflowMonthly(WebRequest request, String requestId) {
		WebResponse response = new WebResponse();

		try {

			Filter filter = request.getFilter();
			int month = filter.getMonth();
			int year = filter.getYear();
			
			System.out.println("Report month : "+month);
			System.out.println("Report year : "+year);

			List<ProductFlow> flowIncome = productFlowRepository.findByTransactionTypeAndPeriod(SELLING.toString(), month, year);
			progressService.sendProgress(1, 1, 20, requestId);
			
			List<ProductFlow> flowCost = productFlowRepository.findByTransactionTypeAndPeriod(PURCHASING.toString(), month, year);
			progressService.sendProgress(1, 1, 20, requestId);
			
			response.setMonthlyDetailIncome(parseCashflows("OUT", flowIncome));
			progressService.sendProgress(1, 1, 20, requestId);
			
			response.setMonthlyDetailCost(parseCashflows("IN", flowCost));
			progressService.sendProgress(1, 1, 20, requestId);
			
			response.setFilter(filter);
			response.setTransactionYears(new int[] { getMinTransactionYear(), Calendar.getInstance().get(Calendar.YEAR) });
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e);
			return WebResponse.failed(e.toString());
		}
		return response;
	}
	
	@Override
	public WebResponse getCashflowDaily(WebRequest request, String requestId) {
		WebResponse response = new WebResponse();
		
		try {
			
			if(request.getFilter() == null) {
				return response;
			}
			
			Filter filter 	= request.getFilter();
			
			int day 		= filter.getDay();
			int month 		= filter.getMonth();
			int year		= filter.getYear(); 
			TransactionType type		= SELLING;// filter.getModule();
			
			List<ProductFlow> productSold = productFlowRepository.findByTransactionTypeAndPeriod(type.toString(), day, month, year);
		//	List<ProductFlow> productSupp = productFlowRepository.findByTransactionTypeAndPeriod(type, day, month, year); 
			response.setDailyCashflow(parseDailyCashflows(productSold));
			response.setFilter(filter);
			
			return response ;
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return WebResponse.failed(e.getMessage());
		}
	}
	@Override
	public WebResponse getBalance(WebRequest request) {
		Filter filter = request.getFilter();
		CashBalance balance = cashBalanceService.getBalanceAt(filter.getDay(), filter.getMonth(), filter.getYear());
		return WebResponse.builder().entity(balance).build();
	}
	
	@Override
	public List<Supplier> getProductSupplier(Long id, int limit, int offset) {
		List<Transaction> transactions = transactionRepository.findProductSupplier(id, limit, offset);
		List<Supplier> suppliers = new ArrayList<>();
		
		for (Transaction transaction : transactions) {
			suppliers.add(transaction.getSupplier());
		}
		return suppliers;
	}
	
	@Override
	public Transaction getFirstTransaction(Long productId) {
		List<Transaction> transactions = transactionRepository.findFirstTransaction(productId);
		if (transactions != null && transactions.size() > 0) {
			return transactions.get(0);
		}
		return null;
	}
	
	@Override
	public int getMinTransactionYear() {
		Object result = transactionRepository.findTransactionYearAsc();

		if (result == null) {
			return Calendar.getInstance().get(Calendar.YEAR);
		}
		int resultInt = Integer.parseInt(result.toString());
		return resultInt;
	}
	
	@Override
	public WebResponse getTransactionData(String transactionCode) {
		
		Transaction transaction = transactionRepository.findTop1ByCode(transactionCode);
		if(null == transaction) {
			return WebResponse.failed("Transaction Not Found");
		}
		List<ProductFlow> productFlows = productFlowRepository.findByTransaction_Id(transaction.getId());
		productFlows.forEach(p->{p.setTransaction(null);});
		transaction.setProductFlows(productFlows); 
		
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

	
	////////////////////////////// PRIVATES /////////////////////////////////////////
	
	private CashFlow getCashflowByPeriod(Integer month, Integer year, final TransactionType transactionType) {
		log.info("get cashflow month: {}, year: {}, transactionType: {}", month, year, transactionType);
		
		Object result = productFlowRepository.findCashflowByModuleAndMonthAndYear(String.valueOf(transactionType), month, year);
		CustomEntity customEntitySetting = (CustomEntity) CashFlow.class.getAnnotation(CustomEntity.class);
		String[] propertyOrder = customEntitySetting.propOrder();
		Object[] propertiesArray = (Object[]) result;
		 
		try {
			CashFlow cashflow = CustomRepositoryImpl.fillObject(CashFlow.class, propertiesArray, propertyOrder);
			cashflow.setMonth(month);
			cashflow.setYear(year);
			return  cashflow;
			
		} catch (Exception e) {			
			throw new RuntimeException(e);
		}
		
	}
	
	private Map<Integer, CashFlow> parseCashflows(final String module, List<ProductFlow> productFlows) {

		Map<Integer, CashFlow> result = new HashMap<Integer, CashFlow>();

		/**
		 * filling days
		 */
		for (int i = 1; i <= 31; i++) {
			result.put(i, CashFlow.builder().amount(0L).count(0L).module(module).build());
		}

		if (productFlows == null || productFlows.size() == 0) {
			System.out.println("empty cashflow");
			return result;
		}

		for (ProductFlow productFlow : productFlows) {

			if (productFlow.getTransaction() == null || productFlow.getProduct() == null) {
				continue;
			} 

			final int day 	  = getTransactionDay(productFlow.getTransaction());

			CashFlow currentCashflow = result.get(day);
			CashFlow modifiedCashflow = parseCashflow(productFlow, currentCashflow, module);
			result.put(day, modifiedCashflow);

		}

		return result;
	}
	
	private int getTransactionDay(Transaction transaction) {
		final Date transactionDate = transaction.getTransactionDate();

		final Calendar cal = Calendar.getInstance();
		cal.setTime(transactionDate);

		final int day 	  = cal.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	private CashFlow parseCashflow(ProductFlow productFlow, CashFlow currentCashflow, String module) { 
		final long amount = productFlow.getCount() * productFlow.getPrice(); 
		
		if(null == currentCashflow) {
			currentCashflow = CashFlow.builder()
					.amount(0L).count(0L)
					.module(module)
					.product(productFlow.getProduct()).build();
		}
		
		currentCashflow.setAmount(currentCashflow.getAmount() + amount);
		currentCashflow.setCount(currentCashflow.getCount() + productFlow.getCount());
		
		return currentCashflow;
	}
	
	private Map<String, CashFlow> parseDailyCashflows(List<ProductFlow> productFlows){ 
		
		Map<String, CashFlow> cashflowMap = new HashMap<>();
		
		for (ProductFlow productFlow : productFlows) {
			
			Product product = productFlow.getProduct();
			
			if(product == null) {
				continue;
			}
			
			if(cashflowMap.get(product.getCode()) == null) {
				cashflowMap.put(product.getCode(), CashFlow.builder().product(product).build());
			}
			 
			CashFlow currentCashflow 	= cashflowMap.get(product.getCode()); 

			cashflowMap.put(product.getCode(), parseCashflow(productFlow, currentCashflow, ""));
		}
		
		/**
		 * calculate proportion for chart
		 */
		final Long maxValue = maxCountValue(cashflowMap);
		
		for (String key : cashflowMap.keySet()) {
			Long count = cashflowMap.get(key).getCount();
			double proportion = count.doubleValue() / maxValue.doubleValue() * 100.d;
			cashflowMap.get(key).setProportion(proportion);
		}
		
		
		return cashflowMap;
	}
	
	private static long maxCountValue(Map<String, CashFlow> cashflowMap) { 
		
		long maxValue = Long.MIN_VALUE;
		Set<String> mapKeys = cashflowMap.keySet();
		
		for (String key : mapKeys) {
			long count = cashflowMap.get(key).getCount();
			if(count > maxValue) {
				maxValue = count;
			}
		}
		
		return maxValue;
	}

}
