package com.fajar.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.custom.CashFlow;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.repository.TransactionRepository;

@Service
public class ReportingService {

	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private ProgressService progressService;
	
	public ShopApiResponse getCashFlow(ShopApiRequest request) {

		ShopApiResponse response = new ShopApiResponse();

		// getTransaction
		int month = request.getFilter().getMonth();
		int year = request.getFilter().getYear();
		String module = request.getFilter().getModule();
		CashFlow cashflow = getCashflow(month, year, module);

		if (cashflow != null) {
			cashflow.setYear(request.getFilter().getYear());
			cashflow.setMonth(request.getFilter().getMonth());
			cashflow.setModule(request.getFilter().getModule());
			response.setEntity(cashflow);
		}
		response.setTransactionYears(new int[] { getMinTransactionYear(), Calendar.getInstance().get(Calendar.YEAR) });
		return response;
	}

	private CashFlow getCashflow(Integer month, Integer year, final String module) {

		String sql = " select sum(`product_flow`.count) as count, sum(`product_flow`.count * `product_flow`.price) as price,`transaction`.`type` as module from `product_flow`  "
				+ " LEFT JOIN `transaction` ON  `transaction`.`id` = `product_flow`.`transaction_id`  "
				+ " WHERE  `transaction`.`type` = '$MODULE' and month(`transaction`.transaction_date) =$MM "
				+ " and year(`transaction`.transaction_date) = $YYYY and `transaction`.deleted = false and `product_flow`.deleted = false";

		sql = sql.replace("$YYYY", year.toString()).replace("$MODULE", module).replace("$MM", month.toString());

		Object cashflow = productFlowRepository.getCustomedObjectFromNativeQuery(sql, CashFlow.class);
		return (CashFlow) cashflow;
	}

	public int getMinTransactionYear() {

		Object result = transactionRepository.findTransactionYearAsc();

		if (result == null)
			return Calendar.getInstance().get(Calendar.YEAR);

		int resultInt = Integer.parseInt(result.toString());
		return resultInt;

	}

	public List<Supplier> getProductSupplier(Long id, int limit, int offset) {

		List<Transaction> transactions = transactionRepository.findProductSupplier(id, limit, offset);// .filterAndSort(sqlSelectTransaction,
																										// Transaction.class);
		List<Supplier> suppliers = new ArrayList<>();

		for (Transaction transaction : transactions) {
			suppliers.add(transaction.getSupplier());
		}
		return suppliers;
	}

	public Transaction getFirstTransaction(Long productId) {

		List<Transaction> transactions = transactionRepository.findFirstTransaction(productId);
		if (transactions != null && transactions.size() > 0) {
			return transactions.get(0);
		}
		return null;
	}

	public static List reverse(List arrayList) {
		List reversedArrayList = new ArrayList<>();
		for (int i = arrayList.size() - 1; i >= 0; i--) {

			// Append the elements in reverse order
			reversedArrayList.add(arrayList.get(i));
		}

		// Return the reversed arraylist
		return reversedArrayList;
	}

	public static int getDiffMonth(int m0, int y0, int m1, int y1) {
		int diff = 0;
		for (int i = y0; i <= y1; i++) {

			int beginMonth = 1;

			if (i == y0) {

				beginMonth = m0;
			}

			for (int j = beginMonth; j <= 12; j++) {

				if (i == y1 && j == m1) {

					return diff;
				}
				diff++;
			}

		}
		return diff;
	}

	public static List<int[]> getMonths(Calendar calendar, int diff) {

		Integer currentMonth = calendar.get(Calendar.MONTH) + 1;
		Integer currentYear = calendar.get(Calendar.YEAR);
		List<int[]> periods = new ArrayList<>();
		String monthString = currentMonth >= 10 ? currentMonth.toString() : "0" + currentMonth;

		periods.add(new int[] { currentYear, Integer.parseInt(monthString) });

		for (int i = 1; i <= diff - 1; i++) {
			currentMonth--;
			if (currentMonth <= 0) {
				currentMonth = 12;
				currentYear--;
			}
			monthString = currentMonth >= 10 ? currentMonth.toString() : "0" + currentMonth;
			periods.add(new int[] { currentYear, Integer.parseInt(monthString) });
		}
		return reverse(periods);
	}

	public static void main(String[] args) {
		List<int[]> months = getMonths(Calendar.getInstance(), 20);

		for (int[] string : months) {
			System.out.println(string[0] + "-" + string[1]);
		}

		System.out.println(getDiffMonth(6, 2019, 12, 2020));
	}

	/**
	 * construct hashMap with key: DAY_OF_MONTH and value: cashFlow in that day
	 * 
	 * @param module
	 * @param productFlows
	 * @return
	 */
	private static Map<Integer, CashFlow> parseCashflow(final String module, List<ProductFlow> productFlows) {

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

			final Date transactionDate = productFlow.getTransaction().getTransactionDate();

			Calendar cal = Calendar.getInstance();
			cal.setTime(transactionDate);

			int day = cal.get(Calendar.DAY_OF_MONTH) + 1;
			long amount = productFlow.getCount() * productFlow.getPrice();

			CashFlow currentCashflow = result.get(day);
			if(null == currentCashflow) {
				currentCashflow = CashFlow.builder().amount(0L).count(0L).module(module).build();
			}
			currentCashflow.setAmount(currentCashflow.getAmount() + amount);
			currentCashflow.setCount(currentCashflow.getCount() + productFlow.getCount());

			result.put(day, currentCashflow);

		}

		return result;
	}

	/**
	 * get day by day cashFlow in selected month
	 * 
	 * @param request
	 * @param requestId
	 * @return
	 */
	public ShopApiResponse getCashflowMonthly(ShopApiRequest request, String requestId) {

		ShopApiResponse response = new ShopApiResponse();

		try {

			Filter filter = request.getFilter();
			int month = filter.getMonth();
			int year = filter.getYear();

			List<ProductFlow> flowIncome = productFlowRepository.findByTransactionTypeAndPeriod("OUT", month, year);
			List<ProductFlow> flowCost = productFlowRepository.findByTransactionTypeAndPeriod("IN", month, year);

			response.setMonthlyDetailIncome(parseCashflow("OUT", flowIncome));
			response.setMonthlyDetailCost(parseCashflow("IN", flowCost));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e);
			return ShopApiResponse.failed(e.toString());
		}
		return response;
	}

	public ShopApiResponse getCashflowDetail(ShopApiRequest request, String requestId) {

		progressService.init(requestId);

		int monthFrom = request.getFilter().getMonth();
		int yearFrom = request.getFilter().getYear();
		int monthTo = request.getFilter().getMonthTo();
		int yearTo = request.getFilter().getYearTo();
		int diffMonth = getDiffMonth(monthFrom, yearFrom, monthTo, yearTo);
		Calendar cal = Calendar.getInstance();

		cal.set(request.getFilter().getYearTo(), request.getFilter().getMonthTo(), 1);

		List<int[]> periods = getMonths(cal, diffMonth);
		List<BaseEntity> supplies = new ArrayList<>();
		List<BaseEntity> purchases = new ArrayList<>();
		Long maxValue = 0L;

		for (int[] period : periods) {

			// supply
			CashFlow cashflowSupply = getCashflow(period[1], period[0], "IN");
			cashflowSupply.setMonth(period[1]);
			cashflowSupply.setYear(period[0]);

			supplies.add(cashflowSupply);

			if (cashflowSupply != null  && cashflowSupply.getAmount() > maxValue) {
				maxValue = cashflowSupply.getAmount();
			}

			// purchase
			CashFlow cashflowPurchase = getCashflow(period[1], period[0], "OUT");
			cashflowPurchase.setMonth(period[1]);
			cashflowPurchase.setYear(period[0]);

			purchases.add(cashflowPurchase);

			if (cashflowPurchase != null  
					&& cashflowPurchase.getAmount() > maxValue) {
				maxValue = cashflowPurchase.getAmount();
			}
			progressService.sendProgress(1, periods.size(), 100, false, requestId);

		}
		ShopApiResponse response = new ShopApiResponse();

		response.setMaxValue(maxValue);
		response.setSupplies(supplies);
		response.setPurchases(purchases);
		return response;
	}
	
	
	public ShopApiResponse getCashflowDaily(ShopApiRequest shopApiRequest, String requestId) {
		
		ShopApiResponse response = new ShopApiResponse();
		
		try {
			
			if(shopApiRequest.getFilter() == null) {
				return response;
			}
			
			Filter filter 	= shopApiRequest.getFilter();
			
			int day 		= filter.getDay();
			int month 		= filter.getMonth();
			int year		= filter.getYear(); 
			String type		= "OUT";// filter.getModule();
			
			List<ProductFlow> productSold = productFlowRepository.findByTransactionTypeAndPeriod(type, day, month, year);
		//	List<ProductFlow> productSupp = productFlowRepository.findByTransactionTypeAndPeriod(type, day, month, year); 
			response.setDailyCashflow(parseDailyCashflow(productSold));
			
			return response ;
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ShopApiResponse.failed(e.getMessage());
		}
	}
	
	private static Map<String, CashFlow> parseDailyCashflow(List<ProductFlow> productFlows){ 
		
		Map<String, CashFlow> cashflowMap = new HashMap<>();
		
		for (ProductFlow productFlow : productFlows) {
			
			Product product = productFlow.getProduct();
			
			
			if(product == null) {
				continue;
			}
			
			if(cashflowMap.get(product.getCode()) == null) {
				cashflowMap.put(product.getCode(), CashFlow.builder().product(product).build());
			}
			
			String productCode			= product.getCode();
			CashFlow currentCashflow 	= cashflowMap.get(productCode);
			long amount 				= productFlow.getCount() * productFlow.getPrice();
			
			currentCashflow.setAmount(currentCashflow.getAmount() + amount);
			currentCashflow.setCount(currentCashflow.getCount() + productFlow.getCount());

			cashflowMap.put(productCode, currentCashflow);
		}
		
		return cashflowMap;
	}

}
