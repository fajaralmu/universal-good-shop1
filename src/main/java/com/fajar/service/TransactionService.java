package com.fajar.service;

import java.util.ArrayList;
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
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.ProductFlowStock;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.User;
import com.fajar.entity.custom.CashFlowEntity;
import com.fajar.repository.CustomerRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RepositoryCustom;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.TransactionRepository;
import com.fajar.util.StringUtil;

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
	@Autowired
	private EntityService entityService;
	
	@PostConstruct
	public void editPrice() {
//		List<ProductFlow> flows = productFlowRepository.findByPriceIsNull();
//		for (ProductFlow productFlow : flows) {
//			System.out.print("*");
//			productFlow.setPrice(productFlow.getProduct().getPrice());
//			productFlowRepository.save(productFlow);
//		}
//		System.out.println("done");
	}

	/**
	 * add stock from supplier
	 * 
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
		transaction.setCode(StringUtil.generateRandomNumber(10));
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
	 * 
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
//			String sql = "select * from product_flow left join `transaction` on transaction_id = transaction.id "
//					+ "left join product on product_id = product.id "
//					+ "where transaction.`type` = 'IN' and product.name like '%" + productName + "%' limit 20";

			String sql = "select product_flow.id as flowId, product_flow.count as flowCount, "
					+ "(select sum(count) as total_count from product_flow where flow_ref_id=flowId and deleted!=1) as used,  "
					+ " product_flow.* from product_flow  "
					+ "left join `transaction` on product_flow.transaction_id = transaction.id "
					+ "left join product on product_flow.product_id = product.id "
					+ "where transaction.`type` = 'IN' and product.name like '%" + productName
					+ "%' having(used is null or flowCount-used>0) limit 20";

			List<ProductFlow> productFlows = productFlowRepositoryCustom.filterAndSort(sql, ProductFlow.class);
			List<BaseEntity> entities = new ArrayList<>();
			for (ProductFlow productFlow : productFlows) {
				if (withCount) {
					ProductFlowStock productFlowStock = checkStock(productFlow);
					productFlowStock.setProductFlow(null);
					if (productFlowStock.getRemainingStock() <= 0) {
						continue;
					}
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
	 * 
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
		transaction.setCode(StringUtil.generateRandomNumber(10));
		transaction.setUser(user);
		transaction.setType("OUT");
		transaction.setCustomer(dbCustomer.get());
		try {
			Transaction newTransaction = transactionRepository.save(transaction);
			for (ProductFlow productFlow : productFlows) {
				productFlow.setTransaction(newTransaction);
				productFlow.setPrice(productFlow.getProduct().getPrice());
				productFlow = productFlowRepository.save(productFlow);
			}
			newTransaction.setProductFlows(productFlows);
			return ShopApiResponse.builder().transaction(newTransaction).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("-1").message(ex.getMessage()).build();
		}
	}

//	
	public ShopApiResponse getCashFlow(ShopApiRequest request) {
		ShopApiResponse response = new ShopApiResponse();
		// getTransaction
		String sql =" select sum(`product_flow`.count) as count, sum(`product_flow`.count * `product_flow`.price) as price,`transaction`.`type` as module from `product_flow`  " + 
				" LEFT JOIN `transaction` ON  `transaction`.`id` = `product_flow`.`transaction_id`  " + 
				" WHERE  `transaction`.`type` = '$MODULE' and month(`transaction`.transaction_date) =$MM " + 
				" and year(`transaction`.transaction_date) = $YYYY and `transaction`.deleted = false and `product_flow`.deleted = false";
		
		sql = sql.replace("$YYYY", request.getFilter().getYear().toString())
				.replace("$MODULE", request.getFilter().getModule())
				.replace("$MM", request.getFilter().getMonth().toString());
		
		
		Object cashflow = productFlowRepositoryCustom.getObjectFromNativeQuery(sql, CashFlowEntity.class);
		if(cashflow !=null) {
			response.setEntity((CashFlowEntity) cashflow);
		}
		
		return response;
	}
}
