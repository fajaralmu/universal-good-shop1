package com.fajar.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
import com.fajar.repository.CustomerRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RepositoryCustom;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.TransactionRepository;

@Service
public class TransactionService {

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

	/**
	 * add stock from supplier
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	public ShopApiResponse submitNew(ShopApiRequest request, HttpServletRequest httpRequest) {
		User user = userSessionService.getUser(httpRequest);
		if (null == user) {
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

		for (ProductFlow productFlow : productFlows) {
			Optional<Product> product = productRepository.findById(productFlow.getProduct().getId());
			if (!product.isPresent()) {
				productFlow.setProduct(product.get());

				continue;
			}
		}
		// transaction.set

		transaction.setUser(user);
		transaction.setType("IN");
		transaction.setSupplier(supplier.get());

		try {
			Transaction dbTransaction = transactionRepository.save(transaction);
			for (ProductFlow productFlow : productFlows) {
				productFlow.setTransaction(dbTransaction);
				productFlowRepository.save(productFlow);
			}
			return ShopApiResponse.builder().transaction(dbTransaction).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("-1").message(ex.getMessage()).build();
		}
	}

	/**
	 * check remaining stock of a product flow
	 * @param productFlow
	 * @return
	 */
	private ProductFlowStock checkStock(ProductFlow productFlow) {

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

		System.out.println("RESULT: " + result.getClass());

		Object[] objectList = (Object[]) result;
		Integer total = Integer.parseInt(objectList[0].toString());
		Integer used = objectList[1] == null ? 0 : Integer.parseInt(objectList[1].toString());
		Integer remaining = total - used;

		ProductFlowStock productFlowStock = new ProductFlowStock();
		productFlowStock.setProductFlow(productFlow);
		productFlowStock.setRemainingStock(remaining);
		productFlowStock.setTotalStock(total);
		return productFlowStock;
	}
	
	public ShopApiResponse stockInfo(ShopApiRequest request) {
		ProductFlowStock productFlowStock = checkStock(request.getProductFlow());
		
		return ShopApiResponse.builder().productFlowStock(productFlowStock).build();
	}

	public ShopApiResponse getStocks(ShopApiRequest request, boolean withCount) {
		try {
			String productName = request.getProduct().getName();
			String sql = "select * from product_flow left join `transaction` on transaction_id = transaction.id "
					+ "left join product on product_id = product.id "
					+ "where transaction.`type` = 'IN' and product.name like '%" + productName + "%' limit 20";

			List<ProductFlow> productFlows = productFlowRepositoryCustom.filterAndSort(sql, ProductFlow.class);
			List<BaseEntity> entities = new ArrayList<>();
			for (ProductFlow productFlow : productFlows) {
				if (withCount) {
					ProductFlowStock productFlowStock = checkStock(productFlow);
					productFlowStock.setProductFlow(null);
					productFlow.setProductFlowStock(productFlowStock);
				}
				productFlow.setTransactionId(productFlow.getTransaction().getId());
				entities.add(productFlow);
			}

			return ShopApiResponse.builder().entities(entities).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ShopApiResponse.builder().code("01").message("server error").build();
		}
	}

	/**
	 * add purchase transaction
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	public ShopApiResponse addPurchaseTransaction(ShopApiRequest request, HttpServletRequest httpRequest) {

		User user = userSessionService.getUser(httpRequest);
		if (null == user) {
			return ShopApiResponse.builder().code("01").message("invalid user").build();
		}

		// validate product and customer
		Optional<Customer> dbCustomer = customerRepository.findById(request.getCustomer().getId());
		if (dbCustomer.isPresent() == false) {
			return ShopApiResponse.builder().code("01").message("invalid Customer").build();
		}
		List<ProductFlow> productFlows = request.getProductFlows();
		for (ProductFlow productFlow : productFlows) {
			Optional<ProductFlow> dbFlow = productFlowRepository.findById(productFlow.getFlowReferenceId());

			if (dbFlow.isPresent() == false) {
				continue;
			}
			ProductFlow refFlow = dbFlow.get();

			ProductFlowStock flowStock = checkStock(refFlow);
			Integer remainingStock = flowStock.getRemainingStock();
			if (productFlow.getCount() > remainingStock) {
				continue;
			}
			productFlow.setProduct(refFlow.getProduct());

		}
		Transaction transaction = new Transaction();
		transaction.setUser(user);
		transaction.setType("OUT");
		transaction.setCustomer(dbCustomer.get());
		try {
			Transaction newTransaction = transactionRepository.save(transaction);
			for (ProductFlow productFlow : productFlows) {
				productFlow.setTransaction(newTransaction);
				productFlowRepository.save(productFlow);
			}
			return ShopApiResponse.builder().transaction(newTransaction).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("-1").message(ex.getMessage()).build();
		}
	}
}
