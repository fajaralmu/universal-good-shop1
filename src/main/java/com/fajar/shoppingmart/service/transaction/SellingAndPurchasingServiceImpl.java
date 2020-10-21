package com.fajar.shoppingmart.service.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.TransactionMode;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.InventoryItem;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.CustomerRepository;
import com.fajar.shoppingmart.repository.ProductFlowRepository;
import com.fajar.shoppingmart.repository.ProductRepository;
import com.fajar.shoppingmart.repository.SupplierRepository;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SellingAndPurchasingServiceImpl implements SellingAndPurchasingService {

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

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	private void validateProductFlows(WebRequest request) {
		if (request.getProductFlows() == null || request.getProductFlows().isEmpty()) {

			throw new IllegalArgumentException("Products not found");
		}
	}	

	/**
	 * add stock from supplier
	 * 
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	@Override
	public WebResponse purchaseProduct(WebRequest request, HttpServletRequest httpRequest) {  
		
		try {
			validateProductFlows(request);
			User user = SessionUtil.getUserFromRequest(httpRequest);  
			if(request.getTransaction()!=null) {
				user.setProcessingDate(request.getTransaction().getTransactionDate());
			}
			
			List<ProductFlow> productFlows = request.getProductFlows();   
			Optional<Supplier> supplier = supplierRepository.findById(request.getSupplier().getId());
			
			if (supplier.isPresent() == false) {
				return WebResponse.failed("invalid supplier");
			}
 
			TransactionMode mode = request.getTransaction() == null || request.getTransaction().getMode() == null ? TransactionMode.REGULAR : request.getTransaction().getMode();			
			Transaction savedTransaction = processPurchasing(productFlows, supplier.get(), user, mode);

			return WebResponse.builder().transaction(savedTransaction).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.failed(ex);

		} finally {
			 
		}
	} 

	private Transaction processPurchasing(List<ProductFlow> productFlows, Supplier supplier, User user, TransactionMode mode) {
		 
		String requestId = user.getRequestId();  

		sendProgress(1, 1, 10, false, requestId);
		
		// check if product is exist
		for (ProductFlow productFlow : productFlows) {
			Optional<Product> product = productRepository.findById(productFlow.getProduct().getId());
			sendProgress(1, productFlows.size(), 10, false, requestId);
			if (!product.isPresent()) {
				throw new RuntimeException("One of product is invalid");
			}
			productFlow.setProduct(product.get());
		}
		 
		Transaction savedTransaction = productInventoryService.savePurchasingTransaction(productFlows, user,supplier, mode);
		return savedTransaction;
	}
	
	@Override
	public WebResponse getStocksByProductName(WebRequest request, String requestId) { 

		List<BaseEntity> productFlows = getProductFlowsByProductName(request.getProduct(),  requestId);

		if (productFlows == null) {
			progressService.sendComplete(requestId);
			return WebResponse.builder().code("01").message("Fetching error").build();
		}

		progressService.sendComplete(requestId);
		return WebResponse.builder().entities(productFlows).build();
	}
	
	private List<BaseEntity> getProductFlowsByProductName(Product product, String requestId) {
		return getProductFlowsByProduct("name", product.getName(),   20,
				NOT_EXACTS, requestId);
	}

	private List<BaseEntity> getProductFlowsByProduct(String key, Object value, int limit,
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
	
	@Override
	public WebResponse sellProduct(WebRequest request, HttpServletRequest httpRequest) {  
		
		try {
			validateProductFlows(request);
			User user = SessionUtil.getUserFromRequest(httpRequest);
			if(request.getTransaction()!=null) {
				user.setProcessingDate(request.getTransaction().getTransactionDate());
			}
			Optional<Customer> dbCustomer = customerRepository.findById(request.getCustomer().getId());

			if (dbCustomer.isPresent() == false) {
				return WebResponse.failed("invalid customer");
			}
			TransactionMode mode = request.getTransaction() == null || request.getTransaction().getMode() == null ? TransactionMode.REGULAR : request.getTransaction().getMode();
			Transaction transaction = processSelling(request.getProductFlows(), dbCustomer.get(), user, mode);
			return WebResponse.builder().transaction(transaction).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.failed(ex);

		} finally { 
		}
	}
	
	private Transaction processSelling(List<ProductFlow> productFlows, Customer customer, User user, TransactionMode mode) {
		String requestId = user.getRequestId();
		sendProgress(1, 1, 10, false, requestId); 

		for (ProductFlow productFlow : productFlows) {

			Optional<Product> dbProduct = productRepository.findById(productFlow.getProduct().getId());

			if (dbProduct.isPresent() == false) {
				throw new RuntimeException("One of product is invalid");
			}
			int remainingStock = productInventoryService.getProductInventory(dbProduct.get());
			int sellingQty = productFlow.getCount();

			if (sellingQty > remainingStock) {
				throw new RuntimeException("One of product is insufficient");
			} else {
				productFlow.setProduct(dbProduct.get());
			} 
			sendProgress(1, productFlows.size(), 10, false, requestId);
		}

		// save to DB  
		Transaction transaction = productInventoryService.saveSellingTransaction( productFlows, user,customer, mode);
		return transaction;
	}

	private void sendProgress(int progress, int maxProgress, int percent, boolean newProgress, String requestId) {

		progressService.sendProgress(progress, maxProgress, percent, newProgress, requestId);

	}

	 

  
}
