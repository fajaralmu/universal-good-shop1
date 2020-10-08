package com.fajar.shoppingmart.service.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.InventoryItem;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.ProductFlowStock;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.CustomerRepository;
import com.fajar.shoppingmart.repository.ProductFlowRepository;
import com.fajar.shoppingmart.repository.ProductRepository;
import com.fajar.shoppingmart.repository.RepositoryCustomImpl;
import com.fajar.shoppingmart.repository.SupplierRepository;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.ReportingService;
import com.fajar.shoppingmart.service.entity.EntityValidation;
import com.fajar.shoppingmart.service.financial.CashBalanceService;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

	private static final boolean NOT_EXACTS = false;
	// private static final boolean MATCH = true;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private SupplierRepository supplierRepository; 
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private ProductInventoryService productInventoryService;
	@Autowired
	private ReportingService reportingService;
	@Autowired
	private RepositoryCustomImpl repositoryCustomImpl;
	@Autowired
	private CashBalanceService cashBalanceService;

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
	public WebResponse purchaseProduct(WebRequest request, HttpServletRequest httpRequest) {  
		User user = SessionUtil.getUserFromRequest(httpRequest);
		
		if (null == user) {
			return WebResponse.invalidSession();
		}

		// begin transaction
		if (request.getProductFlows() == null || request.getProductFlows().isEmpty()) {

			return WebResponse.failed("Product is empty");
		}

		try {
			List<ProductFlow> productFlows = request.getProductFlows();
			String requestId = user.getRequestId();

			// validate supplier
			Optional<Supplier> supplier = supplierRepository.findById(request.getSupplier().getId());
			
			if (supplier.isPresent() == false) {
				return WebResponse.failed("supplier is empty");
			}

			sendProgress(1, 1, 10, false, requestId);
			
			// check if product is exist
			for (ProductFlow productFlow : productFlows) {
				Optional<Product> product = productRepository.findById(productFlow.getProduct().getId());
				sendProgress(1, productFlows.size(), 10, false, requestId);
				if (!product.isPresent()) {
					return WebResponse.failedResponse();
				}
				productFlow.setProduct(product.get());
			}
 
			Transaction savedTransaction = productInventoryService.savePurchasingTransaction(productFlows, user,
					supplier.get(), new Date());

			return WebResponse.builder().transaction(savedTransaction).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.builder().code("01").message(ex.getMessage()).build();

		} finally {
			 
		}
	}
 

	/**
	 * get stock for product list
	 * 
	 * @param products
	 * @param withCount
	 * @param requestId
	 * @return
	 */
	public List<Product> populateProductWithStocks(List<Product> products, boolean withCount, String requestId) {

		for (Product product : products) {
			int totalCount = 0;
			int usedCount = 0;

			Object resultUsedProduct = productFlowRepository.findProductFlowCount(TransactionType.OUT.toString(),
					product.getId());

			Object resultTotalProduct = productFlowRepository.findProductFlowCount(TransactionType.IN.toString(),
					product.getId());

			int remainingCount = 0;
			try {
				usedCount = Integer.parseInt(resultUsedProduct.toString());
			} catch (Exception ex) {
				usedCount = 0;
				ex.printStackTrace();
			}
			try {
				totalCount = Integer.parseInt(resultTotalProduct.toString());
			} catch (Exception ex) {
				totalCount = 0;
				ex.printStackTrace();
			}
			if (totalCount - usedCount > 0) {
				remainingCount = totalCount - usedCount;
			}

			product.setCount(remainingCount);
			sendProgress(1, products.size(), 30, false, requestId);
		}

		return products;
	}

	public WebResponse getStocksByProductName(WebRequest request, boolean withCount, String requestId) { 

		List<BaseEntity> productFlows = getProductFlowsByProductName(request.getProduct(), withCount, requestId);

		if (productFlows == null) {
			progressService.sendComplete(requestId);
			return WebResponse.builder().code("01").message("Fetching error").build();
		}

		progressService.sendComplete(requestId);
		return WebResponse.builder().entities(productFlows).build();
	}
	
	private List<BaseEntity> getProductFlowsByProductName(Product product,  boolean withCount, String requestId) {
		return getProductFlowsByProduct("name", product.getName(), withCount, 20,
				NOT_EXACTS, requestId);
	}

	private List<BaseEntity> getProductFlowsByProduct(String key, Object value, boolean withCount, int limit,
			boolean match, String requestId) {
		try {

			List<ProductFlow> productFlows = new ArrayList<>();
			List<InventoryItem> inventories = productInventoryService.getInventoriesByProduct(key, value, match, limit);

			sendProgress(1, 1, 10, false, requestId);
			log.info("inventories: {}", inventories.size());
			
			for (InventoryItem inventoryItem : inventories) {
				Optional<ProductFlow> productFlow = productFlowRepository.findById(inventoryItem.getIncomingFlowId());
				sendProgress(1, inventories.size(), 90, false, requestId);

				if (productFlow.isPresent() == false)
					continue;
				productFlows.add(productFlow.get());
			}

			log.info("productFlows: {}", productFlows.size());
			return CollectionUtil.convertList(productFlows);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public WebResponse sellProduct(WebRequest request, HttpServletRequest httpRequest) { 

		User user = SessionUtil.getUserFromRequest(httpRequest);

		if (null == user) {
			return WebResponse.invalidSession();
		} 

		try {

			//validate products and customer 
			String requestId = user.getRequestId();
			Optional<Customer> dbCustomer = customerRepository.findById(request.getCustomer().getId());

			if (dbCustomer.isPresent() == false) {
				return WebResponse.builder().code("01").message("invalid Customer").build();
			}

			sendProgress(1, 1, 10, false, requestId);

			//validate product stock 
			List<ProductFlow> productFlows = request.getProductFlows();

			for (ProductFlow productFlow : productFlows) {

				Optional<Product> dbProduct = productRepository.findById(productFlow.getProduct().getId());

				if (dbProduct.isPresent() == false) {
					continue;
				}
				int remainingStock = productInventoryService.getProductInventory(dbProduct.get());
				int sellingQty = productFlow.getCount();

				if (sellingQty > remainingStock) {
					continue;
				} else {
					productFlow.setProduct(dbProduct.get());
				} 
				sendProgress(1, productFlows.size(), 10, false, requestId);
			}

			// save to DB  
			Transaction newTransaction = productInventoryService.saveSellingTransaction(new Date(), productFlows, user,
					dbCustomer.get());
			return WebResponse.builder().transaction(newTransaction).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.builder().code("-1").message(ex.getMessage()).build();

		} finally { 
		}
	}

	private void sendProgress(int progress, int maxProgress, int percent, boolean newProgress, String requestId) {

		progressService.sendProgress(progress, maxProgress, percent, newProgress, requestId);

	}

	/**
	 * ===================================== ============= REPORTING =============
	 * =====================================
	 * 
	 */

	public List<Supplier> getProductSupplier(Long id, int limit, int offset) {
		return reportingService.getProductSupplier(id, limit, offset);
	}

	public Transaction getFirstTransaction(Long id) {
		return reportingService.getFirstTransaction(id);
	}

	public int getMinTransactionYear() {
		return reportingService.getMinTransactionYear();
	}

	public WebResponse getCashFlow(WebRequest request) {
		return reportingService.getCashFlow(request);
	}

	public WebResponse getCashflowDetail(WebRequest request, String requestId) {
		return reportingService.getCashflowDetail(request, requestId);
	}

	public WebResponse getCashflowMonthly(WebRequest request, String requestId) {
		return reportingService.getCashflowMonthly(request, requestId);
	}

	public WebResponse getCashflowDaily(WebRequest request, String requestId) {
		return reportingService.getCashflowDaily(request, requestId);
	}

	public WebResponse getBalance(WebRequest request) {
		Filter filter = request.getFilter();
		CashBalance balance = cashBalanceService.getBalanceAt(filter.getDay(), filter.getMonth(), filter.getYear());
		return WebResponse.builder().entity(balance).build();
	}

}
