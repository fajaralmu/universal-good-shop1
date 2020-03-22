package com.fajar.service;

import java.util.ArrayList;
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
import com.fajar.entity.InventoryItem;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.ProductFlowStock;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.User;
import com.fajar.repository.CustomerRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.SupplierRepository;
import com.fajar.util.CollectionUtil;
import com.fajar.util.EntityUtil;

@Service
public class TransactionService {

	private static final boolean NOT_EXACTS = false;
	private static final boolean MATCH = true; 
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
	@Autowired
	private ReportingService reportingService;

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
		 * save to DB
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

	/**
	 * 
	 * ============= REPORTING =============
	 * 
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

	public ShopApiResponse getCashFlow(ShopApiRequest request) { 
		return reportingService.getCashFlow(request);
	}

	public ShopApiResponse getCashflowDetail(ShopApiRequest request, String requestId) { 
		return reportingService.getCashflowDetail(request, requestId);
	}

	public ShopApiResponse getCashflowMonthly(ShopApiRequest request, String requestId) { 
		return reportingService.getCashflowMonthly(request, requestId);
	} 
	
	public ShopApiResponse getCashflowDaily(ShopApiRequest request, String requestId) {
		return reportingService.getCashflowDaily(request, requestId);
	}
	
}
