package com.fajar.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Customer;
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
import com.fajar.repository.RepositoryCustom;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.TransactionRepository;
import com.fajar.util.EntityUtil;
import com.fajar.util.StringUtil;

@Service
public class TransactionService {

	private static final boolean NOT_EXACTS = false;
	private static final boolean EXACTS = true;
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
	private RepositoryCustom<ProductFlow> productFlowRepositoryCustom;
	@Autowired
	private RepositoryCustom<Transaction> transactionCustomRespositoryCustom;
	@Autowired
	private EntityService entityService;
	@Autowired
	private ProgressService progressService;

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
		Transaction transaction = new Transaction();
		List<ProductFlow> productFlows = request.getProductFlows();
		Optional<Supplier> supplier = supplierRepository.findById(request.getSupplier().getId());
		if (!supplier.isPresent()) {
			return ShopApiResponse.builder().code("01").message("supplier is empty").build();
		}
		progressService.sendProgress(1, 1, 10, false, requestId);
		for (ProductFlow productFlow : productFlows) {
			Optional<Product> product = productRepository.findById(productFlow.getProduct().getId());
			progressService.sendProgress(1, productFlows.size(), 40, false, requestId);
			if (!product.isPresent()) {
				productFlow.setProduct(product.get());

				continue;
			}
		}
		// transaction.set
		transaction.setCode(StringUtil.generateRandomNumber(10));
		transaction.setUser(user);
		transaction.setType("IN");
		transaction.setTransactionDate(new Date());
		transaction.setSupplier(supplier.get());

		try {
			Transaction dbTransaction = transactionRepository.save(transaction);
			progressService.sendProgress(1, 1, 10, false, requestId);
			for (ProductFlow productFlow : productFlows) {
				productFlow.setTransaction(dbTransaction);
				productFlowRepository.save(productFlow);
				progressService.sendProgress(1, productFlows.size(), 40, false, requestId);
			}

			return ShopApiResponse.builder().transaction(dbTransaction).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("-1").message(ex.getMessage()).build();
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

		String sql = "select (select `count` from product_flow where id=$FLOW_ID) as total, "
				+ "(select sum(count) as total_count from product_flow where flow_ref_id=$FLOW_ID and deleted!=1) as used ";
		sql = sql.replace("$FLOW_ID", productFlow.getId().toString());
		Query query = productFlowRepositoryCustom.createNativeQuery(sql);
		Object result = query.getSingleResult();

		Object[] objectList = (Object[]) result;
		Integer total = objectList[0] == null ? 0 : Integer.parseInt(objectList[0].toString());
		Integer used = objectList[1] == null ? 0 : Integer.parseInt(objectList[1].toString());
		Integer remaining = total - used;

		System.out.println(productFlow.getId() + " TOTAL: " + total + ", used: " + used + ". sql: " + sql);
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
		productFlowStock.getProductFlow().getTransaction().setUser(null);
		Product product = productFlowStock.getProductFlow().getProduct();
		productFlowStock.getProductFlow().setProduct(EntityUtil.validateDefaultValue(product));
		return ShopApiResponse.builder().productFlowStock(productFlowStock).build();
	}

	public List<Product> populateProductWithStocks(List<Product> products, boolean withCount, String requestId) {

		for (Product product : products) {
			int totalCount = 0;
			int used = 0;
			final String sqlGetUsedCount = " select sum(product_flow.count) as used from product_flow "
					+ " left join product on product_flow.product_id = product.id "
					+ " left join `transaction` on transaction.id = product_flow.transaction_id "
					+ " where transaction.`type` = 'OUT' and product.id = '$PRODUCT_ID'";
			Object resultUsedProduct = productFlowRepositoryCustom
					.getSingleResult(sqlGetUsedCount.replace("$PRODUCT_ID", product.getId().toString()));

			final String sqlGetTotalCount = "select sum(product_flow.count) as flowCount from product_flow   "
					+ "left join `transaction` on product_flow.transaction_id = transaction.id "
					+ "left join product on product_flow.product_id = product.id  "
					+ "where   transaction.`type` = 'IN' and product.id = '$PRODUCT_ID'";
			Object resultTotalProduct = productFlowRepositoryCustom
					.getSingleResult(sqlGetTotalCount.replace("$PRODUCT_ID", product.getId().toString()));

			Integer remainingCount = 0;
			try {
				used = ((BigDecimal) resultUsedProduct).intValue();
			} catch (Exception ex) {
				used = 0;
				ex.printStackTrace();
				System.out.println("==============ERROR PARSING USED COUNT:" + ex.getMessage());
			}
			try {
				totalCount = ((BigDecimal) resultTotalProduct).intValue();
			} catch (Exception ex) {
				totalCount = 0;
				ex.printStackTrace();
				System.out.println("==============ERROR PARSING TOTAL COUNT:" + ex.getMessage());
			}
			if (totalCount - used > 0) {
				remainingCount = totalCount - used;
			}

			product.setCount(remainingCount);
			System.out.println(product.getCode() + "====================TOTAL: " + totalCount);
			System.out.println("====================USED:" + used);

			progressService.sendProgress(1, products.size(), 30, false, requestId);

		}

		return products;
	}

	public ShopApiResponse getStocksByProductName(ShopApiRequest request, boolean withCount, String requestId) {
		progressService.init(requestId);
		progressService.sendProgress(1, 2, 100, false, requestId);
		List<BaseEntity> productFlows = getProductFlowsByProduct("name", request.getProduct().getName(), withCount, 20,
				NOT_EXACTS);
		if (productFlows == null) {
			progressService.sendComplete(requestId);
			return ShopApiResponse.builder().code("01").message("Fetching error").build();
		}
		progressService.sendProgress(1, 2, 100, false, requestId);
		progressService.sendComplete(requestId);
		return ShopApiResponse.builder().entities(productFlows).build();
	}

	private List<BaseEntity> getProductFlowsByProduct(String key, Object value, boolean withCount, int limit,
			boolean exacts) {
		try {
//			String sql = "select * from product_flow left join `transaction` on transaction_id = transaction.id "
//					+ "left join product on product_id = product.id "
//					+ "where transaction.`type` = 'IN' and product.name like '%" + productName + "%' limit 20";

			String sql = "select product_flow.id as flowId, product_flow.count as flowCount, "
					+ "(select sum(count) as total_count from product_flow where flow_ref_id=flowId and deleted!=1) as used,  "
					+ " product_flow.* from product_flow  "
					+ "left join `transaction` on product_flow.transaction_id = transaction.id "
					+ "left join product on product_flow.product_id = product.id " + "where transaction.`type` = 'IN' "
					+ " and product." + key + " $CONDITION "

					+ "having(used is null or flowCount-used>0) " + (limit > 0 ? " limit " + limit : "");

			String condition = " like '%" + value + "%' ";
			if (exacts) {
				condition = " = '" + value + "'";
			}
			sql = sql.replace("$CONDITION", condition);

			List<ProductFlow> productFlows = productFlowRepositoryCustom.filterAndSort(sql, ProductFlow.class);
			List<BaseEntity> entities = new ArrayList<>();
			for (ProductFlow productFlow : productFlows) {
				/*
				 * if (withCount) { ProductFlowStock productFlowStock =
				 * getSingleStock(productFlow); productFlowStock.setProductFlow(null); if
				 * (productFlowStock.getRemainingStock() <= 0) { continue; }
				 * productFlow.setProductFlowStock(productFlowStock); }
				 * productFlow.setTransactionId(productFlow.getTransaction().getId());
				 */
				entities.add(productFlow);
			}

			return (entities);
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
			if (null == user)
				return ShopApiResponse.builder().code("01").message("invalid user").build();
		}
		progressService.sendProgress(1, 1, 10, false, requestId);

		// validate product and customer
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
				Integer remainingStock = flowStock.getRemainingStock();
				if (productFlow.getCount() > remainingStock) {
					// continue;
				} else {
					productFlow.setProduct(refFlow.getProduct());
				}
			}
			progressService.sendProgress(1, productFlows.size(), 40, false, requestId);

		}
		Transaction transaction = new Transaction();
		transaction.setCode(StringUtil.generateRandomNumber(10));
		transaction.setUser(user);
		transaction.setTransactionDate(new Date());
		transaction.setType("OUT");
		transaction.setCustomer(dbCustomer.get());
		try {
			Transaction newTransaction = transactionRepository.save(transaction);
			progressService.sendProgress(1, 1, 10, false, requestId);
			for (ProductFlow productFlow : productFlows) {
				productFlow.setTransaction(newTransaction);
				productFlow.setPrice(productFlow.getProduct().getPrice());
				productFlow = productFlowRepository.save(productFlow);
				progressService.sendProgress(1, productFlows.size(), 30, false, requestId);
			}
			newTransaction.setProductFlows(productFlows);
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
		CashFlow cashflow = getCashflow(request.getFilter().getMonth(), request.getFilter().getYear(),
				request.getFilter().getModule());
		if (cashflow != null) {
			cashflow.setYear(request.getFilter().getYear());
			cashflow.setMonth(request.getFilter().getMonth());
			response.setEntity(cashflow);
		}
		response.setTransactionYears(getMinAndMaxTransactionYear());
		return response;
	}

	private CashFlow getCashflow(Integer month, Integer year, String module) {
		String sql = " select sum(`product_flow`.count) as count, sum(`product_flow`.count * `product_flow`.price) as price,`transaction`.`type` as module from `product_flow`  "
				+ " LEFT JOIN `transaction` ON  `transaction`.`id` = `product_flow`.`transaction_id`  "
				+ " WHERE  `transaction`.`type` = '$MODULE' and month(`transaction`.transaction_date) =$MM "
				+ " and year(`transaction`.transaction_date) = $YYYY and `transaction`.deleted = false and `product_flow`.deleted = false";

		sql = sql.replace("$YYYY", year.toString()).replace("$MODULE", module).replace("$MM", month.toString());

		Object cashflow = productFlowRepositoryCustom.getCustomedObjectFromNativeQuery(sql, CashFlow.class);
		return (CashFlow) cashflow;
	}

	public int[] getMinAndMaxTransactionYear() {
		Integer minYear = getTransactionYear("asc");
		Integer maxYear = getTransactionYear("desc");
		System.out.println("##MAX YEAR: " + maxYear);
		System.out.println("##MIN YEAR: " + minYear);
		return new int[] { minYear, maxYear };
	}

	private Integer getTransactionYear(String orderType) {
		if (orderType == null || (!orderType.toLowerCase().equals("asc") && !orderType.toLowerCase().equals("desc"))) {
			orderType = "asc";
		}

		String sql = "select year( `transaction`.transaction_date) from `transaction` where `transaction`.transaction_date is not null "
				+ "order by transaction_date " + orderType + " limit 1";
		Object result = productFlowRepositoryCustom.getSingleResult(sql);
		if (result == null || !result.getClass().equals(BigInteger.class))
			return Calendar.getInstance().get(Calendar.YEAR);

		return ((BigInteger) result).intValue();

	}

	public List<Supplier> getProductSupplier(Long id, int limit, int offset) {

		String sqlSelectTransaction = "select * from `transaction` "
				+ "left join product_flow on product_flow.transaction_id = transaction.id "
				+ "where product_flow.product_id = " + id + " and `transaction`.`type` = 'IN' "
				+ "group by supplier_id limit " + limit + " offset " + offset;
		List<Transaction> transactions = transactionCustomRespositoryCustom.filterAndSort(sqlSelectTransaction,
				Transaction.class);
		List<Supplier> suppliers = new ArrayList<>();

		for (Transaction transaction : transactions) {
			suppliers.add(transaction.getSupplier());
		}
		return suppliers;
	}

	public Transaction getFirstTransaction(Long productId) {
		String sql = "select  * from `transaction` left join product_flow on `product_flow`.transaction_id=`transaction`.id  "
				+ "WHERE `product_flow`.product_id =" + productId + "  and `transaction`.`type` = 'IN' "
				+ "order by `transaction`.transaction_date asc limit 1";
		List<Transaction> transactions = transactionCustomRespositoryCustom.filterAndSort(sql, Transaction.class);
		if (transactions != null && transactions.size() > 0) {
			return transactions.get(0);
		}
		return null;
	}

	public static List reverse(List arrayList) {
		ArrayList reversedArrayList = new ArrayList<>();
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
			if (i == y0)
				beginMonth = m0;
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

	public ShopApiResponse getCashflowDetail(ShopApiRequest request, String requestId) {
		progressService.init(requestId);
		int diffMonth = getDiffMonth(request.getFilter().getMonth(), request.getFilter().getYear(),
				request.getFilter().getMonthTo(), request.getFilter().getYearTo());
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
			System.out.println("CASHFLOW SUPPLY: " + cashflowSupply);
			supplies.add(cashflowSupply);
			if (cashflowSupply != null && cashflowSupply.getAmount() != null && cashflowSupply.getAmount() > maxValue) {
				maxValue = cashflowSupply.getAmount();
			}
			// purchase
			CashFlow cashflowPurchase = getCashflow(period[1], period[0], "OUT");

			cashflowPurchase.setMonth(period[1]);
			cashflowPurchase.setYear(period[0]);
			System.out.println("CASHFLOW PURCHASE: " + cashflowPurchase);
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
