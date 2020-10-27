//package com.fajar.shoppingmart.service;
//
//import static com.fajar.shoppingmart.dto.TransactionType.PURCHASING;
//import static com.fajar.shoppingmart.dto.TransactionType.SELLING;
//import static com.fajar.shoppingmart.util.DateUtil.getDiffMonth;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.fajar.shoppingmart.annotation.CustomEntity;
//import com.fajar.shoppingmart.dto.Filter;
//import com.fajar.shoppingmart.dto.TransactionType;
//import com.fajar.shoppingmart.dto.WebRequest;
//import com.fajar.shoppingmart.dto.WebResponse;
//import com.fajar.shoppingmart.entity.BaseEntity;
//import com.fajar.shoppingmart.entity.Product;
//import com.fajar.shoppingmart.entity.ProductFlow;
//import com.fajar.shoppingmart.entity.Supplier;
//import com.fajar.shoppingmart.entity.Transaction;
//import com.fajar.shoppingmart.entity.custom.CashFlow;
//import com.fajar.shoppingmart.repository.CustomRepositoryImpl;
//import com.fajar.shoppingmart.repository.ProductFlowRepository;
//import com.fajar.shoppingmart.repository.TransactionRepository;
//import com.fajar.shoppingmart.util.DateUtil;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Slf4j
//public class ReportingService {
//
//	@Autowired
//	private ProductFlowRepository productFlowRepository; 
//	@Autowired
//	private TransactionRepository transactionRepository;
//	@Autowired
//	private ProgressService progressService;
//	
//	public WebResponse getCashFlow(WebRequest request) {
//
//		WebResponse response = new WebResponse();
//
//		// getTransaction
//		int month = request.getFilter().getMonth();
//		int year = request.getFilter().getYear();
//		String module = request.getFilter().getModule();
//		
//		TransactionType transactionType;
//		switch (module) {
//			case "IN":
//			case "PURCHASING":
//				transactionType = PURCHASING;
//				break;
//			case "OUT":
//			case "SELLING":
//			default:
//				transactionType = SELLING;
//				break;
//		}
//		CashFlow cashflow = getCashflow(month, year, transactionType);
//
//		if (cashflow != null) {
//			cashflow.setYear(request.getFilter().getYear());
//			cashflow.setMonth(request.getFilter().getMonth());
//			cashflow.setModule(request.getFilter().getModule());
//			response.setEntity(cashflow);
//		}
//		response.setTransactionYears(new int[] { getMinTransactionYear(), Calendar.getInstance().get(Calendar.YEAR) });
//		return response;
//	}
//
//	/**
//	 * get cash flow with specified month and year and module
//	 * @param month
//	 * @param year
//	 * @param transactionType
//	 * @return
//	 */
//	private CashFlow getCashflow(Integer month, Integer year, final TransactionType transactionType) {
//		log.info("get cashflow month: {}, year: {}, transactionType: {}", month, year, transactionType);
//		
//		Object result = productFlowRepository.findCashflowByModuleAndMonthAndYear(String.valueOf(transactionType), month, year);
//		CustomEntity customEntitySetting = (CustomEntity) CashFlow.class.getAnnotation(CustomEntity.class);
//		String[] propertyOrder = customEntitySetting.propOrder();
//		Object[] propertiesArray = (Object[]) result;
//		 
//		try {
//			CashFlow cashflow = CustomRepositoryImpl.fillObject(CashFlow.class, propertiesArray, propertyOrder);
//			return  cashflow;
//			
//		} catch (Exception e) {			
//			throw new RuntimeException(e);
//		}
//		
//	}
//
//	/**
//	 * find the first year transaction performed
//	 * @return
//	 */
//	public int getMinTransactionYear() {
//
//		Object result = transactionRepository.findTransactionYearAsc();
//
//		if (result == null) {
//			return Calendar.getInstance().get(Calendar.YEAR);
//		}
//		int resultInt = Integer.parseInt(result.toString());
//		return resultInt;
//
//	}
//
//	public List<Supplier> getProductSupplier(Long id, int limit, int offset) {
//
//		List<Transaction> transactions = transactionRepository.findProductSupplier(id, limit, offset);// .filterAndSort(sqlSelectTransaction,
//																										// Transaction.class);
//		List<Supplier> suppliers = new ArrayList<>();
//
//		for (Transaction transaction : transactions) {
//			suppliers.add(transaction.getSupplier());
//		}
//		return suppliers;
//	}
//
//	public Transaction getFirstTransaction(Long productId) {
//
//		List<Transaction> transactions = transactionRepository.findFirstTransaction(productId);
//		if (transactions != null && transactions.size() > 0) {
//			return transactions.get(0);
//		}
//		return null;
//	} 
//
//	/**
//	 * construct hashMap with key: DAY_OF_MONTH and value: cashFlow in that day
//	 * 
//	 * @param module
//	 * @param productFlows
//	 * @return
//	 */
//	
//
//	/**
//	 * get day by day cashFlow in selected month
//	 * 
//	 * @param request
//	 * @param requestId
//	 * @return
//	 */
//	public WebResponse getCashflowMonthly(WebRequest request, String requestId) {
//
//		WebResponse response = new WebResponse();
//
//		try {
//
//			Filter filter = request.getFilter();
//			int month = filter.getMonth();
//			int year = filter.getYear();
//			
//			System.out.println("Report month : "+month);
//			System.out.println("Report year : "+year);
//
//			List<ProductFlow> flowIncome = productFlowRepository.findByTransactionTypeAndPeriod(SELLING.toString(), month, year);
//			progressService.sendProgress(1, 1, 20, requestId);
//			
//			List<ProductFlow> flowCost = productFlowRepository.findByTransactionTypeAndPeriod(PURCHASING.toString(), month, year);
//			progressService.sendProgress(1, 1, 20, requestId);
//			
//			response.setMonthlyDetailIncome(parseCashflow("OUT", flowIncome));
//			progressService.sendProgress(1, 1, 20, requestId);
//			
//			response.setMonthlyDetailCost(parseCashflow("IN", flowCost));
//			progressService.sendProgress(1, 1, 20, requestId);
//			
//			response.setFilter(filter);
//			response.setTransactionYears(new int[] { getMinTransactionYear(), Calendar.getInstance().get(Calendar.YEAR) });
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Error: " + e);
//			return WebResponse.failed(e.toString());
//		}
//		return response;
//	}
//	
//	
//
//	/**
//	 * get cash flow list by selected range of period
//	 * @param request
//	 * @param requestId
//	 * @return
//	 */
//	public WebResponse getCashflowDetail(WebRequest request, String requestId) { 
//		
//		int monthFrom = request.getFilter().getMonth();
//		int yearFrom = request.getFilter().getYear();
//		int monthTo = request.getFilter().getMonthTo();
//		int yearTo = request.getFilter().getYearTo();
//		
//		int diffMonth = getDiffMonth(monthFrom, yearFrom, monthTo, yearTo);
//		
//		Calendar periodTo = Calendar.getInstance(); 
//		int dayCount = DateUtil.getMonthDayCount(yearTo, monthTo);
//		periodTo.set(request.getFilter().getYearTo(), request.getFilter().getMonthTo() - 1, dayCount);
//
//		List<int[]> periods = DateUtil.getMonths(periodTo, diffMonth);
//		List<BaseEntity> supplies = new ArrayList<>();
//		List<BaseEntity> purchases = new ArrayList<>();
//		Long maxValue = 0L;
//
//		for (int[] period : periods) {
//			System.out.println("o o PERIOD: "+period[0]+", "+period[1]);
//
//			// supply
//			CashFlow cashflowSupply = getCashflow(period[1], period[0], PURCHASING);
//			cashflowSupply.setMonth(period[1]);
//			cashflowSupply.setYear(period[0]);
//
//			supplies.add(cashflowSupply);
//
//			if (cashflowSupply != null  && cashflowSupply.getAmount() > maxValue) {
//				maxValue = cashflowSupply.getAmount();
//			}
//
//			// purchase
//			CashFlow cashflowPurchase = getCashflow(period[1], period[0], SELLING);
//			cashflowPurchase.setMonth(period[1]);
//			cashflowPurchase.setYear(period[0]);
//
//			purchases.add(cashflowPurchase);
//
//			if (cashflowPurchase != null  
//					&& cashflowPurchase.getAmount() > maxValue) {
//				maxValue = cashflowPurchase.getAmount();
//			}
//			progressService.sendProgress(1, periods.size(), 100, false, requestId);
//
//		}
//		WebResponse response = new WebResponse();
//
//		response.setMaxValue(maxValue);
//		response.setSupplies(supplies);
//		response.setPurchases(purchases);
//		 
//		return response;
//	}
//	
//	/**
//	 * get cash flow in specified DAY
//	 * @param shopApiRequest
//	 * @param requestId
//	 * @return
//	 */
//	public WebResponse getCashflowDaily(WebRequest shopApiRequest, String requestId) {
//		
//		WebResponse response = new WebResponse();
//		
//		try {
//			
//			if(shopApiRequest.getFilter() == null) {
//				return response;
//			}
//			
//			Filter filter 	= shopApiRequest.getFilter();
//			
//			int day 		= filter.getDay();
//			int month 		= filter.getMonth();
//			int year		= filter.getYear(); 
//			TransactionType type		= SELLING;// filter.getModule();
//			
//			List<ProductFlow> productSold = productFlowRepository.findByTransactionTypeAndPeriod(type.toString(), day, month, year);
//		//	List<ProductFlow> productSupp = productFlowRepository.findByTransactionTypeAndPeriod(type, day, month, year); 
//			response.setDailyCashflow(parseDailyCashflow(productSold));
//			response.setFilter(filter);
//			
//			return response ;
//			
//		}catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			return WebResponse.failed(e.getMessage());
//		}
//	}
//	
//	private static Map<String, CashFlow> parseDailyCashflow(List<ProductFlow> productFlows){ 
//		
//		Map<String, CashFlow> cashflowMap = new HashMap<>();
//		
//		for (ProductFlow productFlow : productFlows) {
//			
//			Product product = productFlow.getProduct();
//			
//			if(product == null) {
//				continue;
//			}
//			
//			if(cashflowMap.get(product.getCode()) == null) {
//				cashflowMap.put(product.getCode(), CashFlow.builder().product(product).build());
//			}
//			
//			String productCode			= product.getCode();
//			CashFlow currentCashflow 	= cashflowMap.get(productCode);
//			long amount 				= productFlow.getCount() * productFlow.getPrice();
//			
//			currentCashflow.setAmount(currentCashflow.getAmount() + amount);
//			currentCashflow.setCount(currentCashflow.getCount() + productFlow.getCount());
//
//			cashflowMap.put(productCode, currentCashflow);
//		}
//		
//		/**
//		 * calculate proportion for chart
//		 */
//		final Long maxValue = maxCountValue(cashflowMap);
//		
//		for (String key : cashflowMap.keySet()) {
//			Long count = cashflowMap.get(key).getCount();
//			double proportion = count.doubleValue() / maxValue.doubleValue() * 100.d;
//			cashflowMap.get(key).setProportion(proportion);
//		}
//		
//		
//		return cashflowMap;
//	}
//	
//	/**
//	 * get max amount value from cash flow map
//	 * @param cashflowMap
//	 * @return
//	 */
//	private static long maxCountValue(Map<String, CashFlow> cashflowMap) { 
//		
//		long maxValue = Long.MIN_VALUE;
//		Set<String> mapKeys = cashflowMap.keySet();
//		
//		for (String key : mapKeys) {
//			long count = cashflowMap.get(key).getCount();
//			if(count > maxValue) {
//				maxValue = count;
//			}
//		}
//		
//		return maxValue;
//	}
//	
//	public Transaction getTransactionByCode(String code) {
//		Transaction transaction = transactionRepository.findTop1ByCode(code);
//		if(null == transaction) {
//			return null;
//		}
//		List<ProductFlow> productFlows = productFlowRepository.findByTransaction_Id(transaction.getId());
//		productFlows.forEach(p->{p.setTransaction(null);});
//		transaction.setProductFlows(productFlows);
//		return transaction;
//	}
//
//}
