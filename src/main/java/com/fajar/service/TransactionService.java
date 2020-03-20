package com.fajar.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Customer;
import com.fajar.entity.InventoryItem;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.ProductFlowStock;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.User;
import com.fajar.entity.custom.CashFlow;
import com.fajar.repository.CustomerRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.TransactionRepository;
import com.fajar.util.CollectionUtil;
import com.fajar.util.EntityUtil;

@Service
public class TransactionService {

	private static final boolean NOT_EXACTS = false;
	private static final boolean MATCH = true;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private SupplierRepository supplierRepository;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private ProductInventoryService productInventoryService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	/**
	 * add stock from supplier
	 * 
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	public ShopApiResponse supplyProduct(ShopApiRequest request, HttpServletRequest httpRequest, String requestId) {
		
		progressService.init(requestId);
		User user = userSessionService.getUserFromSession(httpRequest);
		
		if (null == user) {
			user = userSessionService.getUserFromRegistry(httpRequest);
			if (null == user)
				return ShopApiResponse.builder().code("01").message("invalid user").build();
		}
		
		if (request.getProductFlows() == null || request.getProductFlows().isEmpty()
				|| !userSessionService.hasSession(httpRequest)) {
			
			return ShopApiResponse.builder().code("01").message("product is empty").build();
		}

		List<ProductFlow> productFlows = request.getProductFlows();
		Optional<Supplier> supplier = supplierRepository.findById(request.getSupplier().getId());
		
		if (!supplier.isPresent()) {
			return ShopApiResponse.builder().code("01").message("supplier is empty").build();
		}
		progressService.sendProgress(1, 1, 10, false, requestId);
		/**
		 * check if product is exist
		 */
		for (ProductFlow productFlow : productFlows) {
			Optional<Product> product = productRepository.findById(productFlow.getProduct().getId());
			progressService.sendProgress(1, productFlows.size(), 40, false, requestId);
			if (!product.isPresent()) {
				return ShopApiResponse.failedResponse();
			}
			productFlow.setProduct(product.get());
		}

		try {
			
			Transaction savedTransaction = productInventoryService.saveSupplyTransaction(productFlows, requestId, user,
					supplier.get(), new Date());
			
			return ShopApiResponse.builder().transaction(savedTransaction).build();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("01").message(ex.getMessage()).build();
			
		} finally {
			progressService.sendComplete(requestId);
		}
	}

	/**
	 * check remaining stock of a product flow
	 * 
	 * @param productFlow
	 * @return
	 */
	private ProductFlowStock getSingleStock(ProductFlow productFlow) {

		// validate product flow

		Optional<ProductFlow> dbProductFlow = productFlowRepository.findByIdAndTransaction_Type(productFlow.getId(),
				"IN");
		if (dbProductFlow.isPresent() == false) {
			return null;
		}
		productFlow = dbProductFlow.get();
		
		productFlow.setTransactionId(productFlow.getTransaction().getId());

		String sql 		= "select (select `count` from product_flow where id=$FLOW_ID) as total, "
							+ "(select sum(count) as total_count from product_flow where flow_ref_id=$FLOW_ID and deleted!=1) as used ";
		sql 			= sql.replace("$FLOW_ID", productFlow.getId().toString());
		
		Query query 	= productFlowRepository.createNativeQuery(sql);
		Object result 	= query.getSingleResult();

		Object[] objectList = (Object[]) result;
		
		Integer total 		= objectList[0] == null ? 0 : Integer.parseInt(objectList[0].toString());
		Integer used 		= objectList[1] == null ? 0 : Integer.parseInt(objectList[1].toString());
		Integer remaining 	= total - used;

		ProductFlowStock productFlowStock = new ProductFlowStock();
		productFlowStock.setProductFlow(productFlow);
		productFlowStock.setUsedStock(used);
		productFlowStock.setRemainingStock(remaining);
		productFlowStock.setTotalStock(total);
		// System.out.println("RESULT getSingleStock: " + result );
		return productFlowStock;
	}

	public ShopApiResponse stockInfo(ShopApiRequest request) {
		ProductFlowStock productFlowStock = getSingleStock(request.getProductFlow());
		
		if (productFlowStock == null) {
			return ShopApiResponse.failedResponse();
		}

		Product product = productFlowStock.getProductFlow().getProduct();
		
		productFlowStock.getProductFlow().getTransaction().setUser(null);
		productFlowStock.getProductFlow().setProduct(EntityUtil.validateDefaultValue(product));
		
		return ShopApiResponse.builder().productFlowStock(productFlowStock).build();
	}

	public List<Product> populateProductWithStocks(List<Product> products, boolean withCount, String requestId) {

		for (Product product : products) {
			int totalCount = 0;
			int used = 0;

			Object resultUsedProduct = productFlowRepository.findFlowCount("OUT", product.getId());

			Object resultTotalProduct = productFlowRepository.findFlowCount("IN", product.getId());

			int remainingCount = 0;
			try {
				used = Integer.parseInt(resultUsedProduct.toString());
			} catch (Exception ex) {
				used = 0;
				ex.printStackTrace();
			}
			try {
				totalCount = Integer.parseInt(resultTotalProduct.toString());
			} catch (Exception ex) {
				totalCount = 0;
				ex.printStackTrace();
			}
			if (totalCount - used > 0) {
				remainingCount = totalCount - used;
			}

			product.setCount(remainingCount);

			progressService.sendProgress(1, products.size(), 30, false, requestId);

		}

		return products;
	}

	public ShopApiResponse getStocksByProductName(ShopApiRequest request, boolean withCount, String requestId) {
		progressService.init(requestId);

		List<BaseEntity> productFlows = getProductFlowsByProduct("name", request.getProduct().getName(), withCount, 20,
				NOT_EXACTS, requestId);

		if (productFlows == null) {
			progressService.sendComplete(requestId);
			return ShopApiResponse.builder().code("01").message("Fetching error").build();
		}

		progressService.sendComplete(requestId);
		return ShopApiResponse.builder().entities(productFlows).build();
	}

	private List<BaseEntity> getProductFlowsByProduct(String key, Object value, boolean withCount, int limit,
			boolean match, String requestId) {
		try {

			List<ProductFlow> productFlows = new ArrayList<>();
			List<InventoryItem> inventories = productInventoryService.getInventoriesByProduct(key, value, match, limit);

			progressService.sendProgress(1, 1, 10, false, requestId);

			for (InventoryItem inventoryItem : inventories) {
				Optional<ProductFlow> productFlow = productFlowRepository.findById(inventoryItem.getIncomingFlowId());
				progressService.sendProgress(1, inventories.size(), 90, false, requestId);

				if (productFlow.isPresent() == false)
					continue;
				productFlows.add(productFlow.get());
			}

			return CollectionUtil.convertList(productFlows);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * add purchase transaction
	 * 
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	public ShopApiResponse addPurchaseTransaction(ShopApiRequest request, HttpServletRequest httpRequest,
			String requestId) {
		
		progressService.init(requestId);
		
		User user = userSessionService.getUserFromSession(httpRequest);
		
		if (null == user) {
			user = userSessionService.getUserFromRegistry(httpRequest);
			
			if (null == user) {
				return ShopApiResponse.builder().code("01").message("invalid user").build();
			}
			
		}
		progressService.sendProgress(1, 1, 10, false, requestId);

		/**
		 * validate products and customer
		 */
		Optional<Customer> dbCustomer = customerRepository.findById(request.getCustomer().getId());
		
		if (dbCustomer.isPresent() == false) {
			return ShopApiResponse.builder().code("01").message("invalid Customer").build();
		}
		
		progressService.sendProgress(1, 1, 10, false, requestId);
		
		List<ProductFlow> productFlows = request.getProductFlows();
		
		for (ProductFlow productFlow : productFlows) {
			Optional<ProductFlow> dbFlow = productFlowRepository.findById(productFlow.getFlowReferenceId());

			if (dbFlow.isPresent() == false) {
				// continue;
			} else {
				ProductFlow refFlow = dbFlow.get();
				ProductFlowStock flowStock = getSingleStock(refFlow);
				
				if (null == flowStock) {
					flowStock = new ProductFlowStock();
				}
				
				Integer remainingStock = flowStock.getRemainingStock();
				
				if (productFlow.getCount() > remainingStock) {
					// continue;
				} else {
					productFlow.setProduct(refFlow.getProduct());
				}
			}
			progressService.sendProgress(1, productFlows.size(), 40, false, requestId);

		}

		/**
		 * save to db
		 */
		try {
			Transaction newTransaction = productInventoryService.savePurchaseTransaction(new Date(), productFlows,
					requestId, user, dbCustomer.get());
			return ShopApiResponse.builder().transaction(newTransaction).build();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("-1").message(ex.getMessage()).build();
			
		} finally {
			progressService.sendComplete(requestId);
		}
	}

//	
	public ShopApiResponse getCashFlow(ShopApiRequest request) {
		
		ShopApiResponse response = new ShopApiResponse();
		
		// getTransaction
		int month			= request.getFilter().getMonth();
		int year			= request.getFilter().getYear();
		String module		= request.getFilter().getModule();
		CashFlow cashflow 	= getCashflow(month, year, module);
		
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

		Integer currentMonth 	= calendar.get(Calendar.MONTH) + 1;
		Integer currentYear 	= calendar.get(Calendar.YEAR);
		List<int[]> periods 	= new ArrayList<>();
		String monthString 		= currentMonth >= 10 ? currentMonth.toString() : "0" + currentMonth;
		
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

			if (productFlow.getTransaction() == null) {
				continue;
			}

			final Date transactionDate = productFlow.getTransaction().getTransactionDate();

			Calendar cal = Calendar.getInstance();
			cal.setTime(transactionDate);
			
			int day 	= cal.get(Calendar.DAY_OF_MONTH) + 1;
			long amount = productFlow.getCount() * productFlow.getPrice();
			
			CashFlow currentCashflow = result.get(day);
			currentCashflow.setAmount(currentCashflow.getAmount() + amount);
			currentCashflow.setCount(currentCashflow.getCount()+productFlow.getCount());
			
			result.put(day, currentCashflow);

		}

		return result;
	}

	/**
	 * get day by day cashFlow in selected month
	 * @param request
	 * @param requestId
	 * @return
	 */
	public ShopApiResponse getCashflowMonthly(ShopApiRequest request, String requestId) {

		ShopApiResponse response = new ShopApiResponse();

		try {

			Filter filter 	= request.getFilter();
			int month 		= filter.getMonth();
			int year 		= filter.getYear();

			List<ProductFlow> flowIncome = productFlowRepository.findByTransactionTypeAndPeriod("OUT", month, year);
			List<ProductFlow> flowCost = productFlowRepository.findByTransactionTypeAndPeriod("IN", month, year); 
			
			response.setMonthlyDetailIncome(parseCashflow("OUT", flowIncome));
			response.setMonthlyDetailCost(parseCashflow("IN", flowCost));
			
		} catch (Exception e) {

			System.out.println("Error: " + e);
			return ShopApiResponse.failed(e.getMessage());
		}
		return response;
	}

	public ShopApiResponse getCashflowDetail(ShopApiRequest request, String requestId) {
		
		progressService.init(requestId);
		
		int monthFrom	= request.getFilter().getMonth();
		int yearFrom	= request.getFilter().getYear();
		int monthTo		= request.getFilter().getMonthTo();
		int yearTo		= request.getFilter().getYearTo();		
		int diffMonth	= getDiffMonth(monthFrom, yearFrom, monthTo, yearTo); 
		Calendar cal	= Calendar.getInstance();
		
		cal.set(request.getFilter().getYearTo(), request.getFilter().getMonthTo(), 1);
		
		List<int[]> periods 		= getMonths(cal, diffMonth);
		List<BaseEntity> supplies 	= new ArrayList<>();
		List<BaseEntity> purchases 	= new ArrayList<>();
		Long maxValue 				= 0L;

		for (int[] period : periods) {

			// supply
			CashFlow cashflowSupply = getCashflow(period[1], period[0], "IN");
			cashflowSupply.setMonth(period[1]);
			cashflowSupply.setYear(period[0]);

			supplies.add(cashflowSupply);

			if (cashflowSupply != null && cashflowSupply.getAmount() != null && cashflowSupply.getAmount() > maxValue) {
				maxValue = cashflowSupply.getAmount();
			}

			// purchase
			CashFlow cashflowPurchase = getCashflow(period[1], period[0], "OUT");
			cashflowPurchase.setMonth(period[1]);
			cashflowPurchase.setYear(period[0]);

			purchases.add(cashflowPurchase);

			if (cashflowPurchase != null && cashflowPurchase.getAmount() != null
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
}
