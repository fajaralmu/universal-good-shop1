package com.fajar.shoppingmart.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.InventoryItem;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.EntityRepository;
import com.fajar.shoppingmart.repository.InventoryItemRepository;
import com.fajar.shoppingmart.repository.PersistenceOperation;
import com.fajar.shoppingmart.repository.RepositoryCustomImpl;
import com.fajar.shoppingmart.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductInventoryService {

	@Autowired
	private InventoryItemRepository inventoryItemRepository; 
	@Autowired
	private ProgressService progressService;
	@Autowired
	private CashBalanceService cashBalanceService;
	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private RepositoryCustomImpl repositoryCustom;

	public static final TransactionType TYPE_OUT = TransactionType.OUT;
	public static final TransactionType TYPE_IN = TransactionType.IN;

	/**
	 * create common transaction object
	 * 
	 * @param type
	 * @param user
	 * @param customer
	 * @param supplier
	 * @param date
	 * @return
	 */
	private Transaction buildTransactionObject(TransactionType type, User user, Customer customer, Supplier supplier,
			Date date) {
		Transaction transaction = new Transaction();
		transaction.setCode(StringUtil.generateRandomNumber(10));
		transaction.setUser(user);
		transaction.setType(type);
		transaction.setTransactionDate(date);

		if (TYPE_IN.equals(type)) {
			transaction.setSupplier(supplier);
		} else if (TYPE_OUT.equals(type)) {
			transaction.setCustomer(customer);
		}

		return transaction;
	}

	/**
	 * adding product to the shop from supplier
	 * 
	 * @param productFlows
	 * @param requestId
	 * @param user
	 * @param supplier
	 * @param transactionData
	 * @return
	 */
	public Transaction saveSupplyTransaction(List<ProductFlow> productFlows, String requestId, User user,
			Supplier supplier, Date transactionData) {
		final Transaction transaction = buildTransactionObject(TYPE_IN, user, null, supplier, transactionData);

		PersistenceOperation persistenceOperation = new PersistenceOperation() {

			@Override
			public void doPersist(Session hibernateSession) {

				Long newTransactionId = (Long) hibernateSession.save(transaction);
				transaction.setId(newTransactionId);

				progressService.sendProgress(1, 1, 10, false, requestId);

				for (ProductFlow productFlow : productFlows) {

					productFlow.setId(null);// never update
					// IMPORTANT!!
					productFlow.setTransaction(transaction);
					Long productFlowId = (Long) hibernateSession.save(productFlow);
					productFlow.setId(productFlowId);

					/**
					 * update cash balance
					 */
					cashBalanceService.updateCashBalance(productFlow);

					/**
					 * INSERT new inventory item row
					 */
					InventoryItem inventoryItem = new InventoryItem(productFlow);
					inventoryItem.addNewProduct();
					hibernateSession.save(inventoryItem);

					/**
					 * inventory item NEW VERSION
					 */
					InventoryItem inventoryItemV2 = inventoryItemRepository
							.findTop1ByProduct_IdAndNewVersion(productFlow.getProduct().getId(), true);

					if (null == inventoryItemV2) {
						inventoryItemV2 = new InventoryItem();
						inventoryItemV2.setProduct(productFlow.getProduct());
						inventoryItemV2.setCount(productFlow.getCount());
					} else {
						int currentCount = inventoryItemV2.getCount();
						int finalCount = currentCount + productFlow.getCount();
						inventoryItemV2.setCount(finalCount);
					}

					hibernateSession.merge(inventoryItemV2);

					progressService.sendProgress(1, productFlows.size(), 40, false, requestId);
				}
			}
		};

		repositoryCustom.pesistOperation(persistenceOperation);
		transaction.setProductFlows(productFlows);
		return transaction;

	}

	/**
	 * customer buy product from shop
	 * 
	 * @param d
	 * @param productFlows
	 * @param requestId
	 * @param user
	 * @param customer
	 * @return
	 */
	public synchronized Transaction savePurchaseTransaction(Date d, List<ProductFlow> productFlows, String requestId,
			User user, Customer customer) {
		if (productFlows == null || productFlows.size() == 0) {
			throw new RuntimeException("INVALID PRODUCTS");
		}
	 
		try {
			Transaction transaction = buildTransactionObject(TYPE_OUT, user, customer, null, d);
			Transaction newTransaction = entityRepository.save(transaction);
			progressService.sendProgress(1, 1, 10, false, requestId);
			int purchasedProduct = 0;
			for (ProductFlow productFlow : productFlows) {
				if (productFlow.getCount() <= 0)
					continue;
				/**
				 * IMPORTANT!!
				 * 
				 */
				productFlow.setTransaction(newTransaction);
				productFlow.setPrice(productFlow.getProduct().getPrice());

				/**
				 * UPDATE inventory item row
				 */
				InventoryItem inventoryItem = inventoryItemRepository
						.findByIncomingFlowId(productFlow.getFlowReferenceId());
				InventoryItem inventoryItemV2 = inventoryItemRepository
						.findTop1ByProduct_IdAndNewVersion(productFlow.getProduct().getId(), true);

				if (null == inventoryItem) {
					throw new RuntimeException("Inventory Item not found:" + productFlow.getFlowReferenceId());
				}

				/**
				 * save NEW productFlow record to database
				 */
				productFlow = entityRepository.save(productFlow);

				/**
				 * update cash balance
				 */
				cashBalanceService.updateCashBalance(productFlow);

				/**
				 * update count
				 */
				inventoryItem.setCount(inventoryItem.getCount() - productFlow.getCount());

				if (inventoryItem.getCount() < 0) {
					// SKIP
					continue;
				}

				entityRepository.save(inventoryItem);

				/**
				 * inventory item NEW VERSION
				 */
				int currentCount = inventoryItemV2.getCount();
				int finalCount = currentCount - productFlow.getCount();
				if (finalCount < 0) {
					// SKIP
					continue;
				}
				inventoryItemV2.setCount(finalCount);

				entityRepository.save(inventoryItemV2);

				purchasedProduct++;
				progressService.sendProgress(1, productFlows.size(), 30, false, requestId);

			}
			System.out.println("PURCHASES PRODUCTS: " + purchasedProduct);
			newTransaction.setProductFlows(productFlows);
			return newTransaction;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			progressService.sendComplete(requestId);
		}
	}

	public synchronized Transaction savePurchaseTransactionV2(Date transactionDate, List<ProductFlow> productFlows,
			String requestId, User user, Customer customer) {
		if (productFlows == null || productFlows.size() == 0) {
			throw new RuntimeException("INVALID PRODUCTS");
		}
		final Transaction transaction = buildTransactionObject(TYPE_OUT, user, customer, null, transactionDate);

		PersistenceOperation persistenceOperation = new PersistenceOperation() {

			@Override
			public void doPersist(Session hibernateSession) {
				Long newTransactionId = (Long) hibernateSession.save(transaction);
				transaction.setId(newTransactionId);

				progressService.sendProgress(1, 1, 10, false, requestId);
				int purchasedProduct = 0;

				for (ProductFlow productFlow : productFlows) {
					if (productFlow.getCount() <= 0)
						continue;
					/**
					 * IMPORTANT!!
					 * 
					 */
					productFlow.setTransaction(transaction);
					productFlow.setPrice(productFlow.getProduct().getPrice());

					/**
					 * UPDATE inventory item row
					 */
//					InventoryItem inventoryItem = inventoryItemRepository
//							.findByIncomingFlowId(productFlow.getFlowReferenceId());
					InventoryItem inventoryItemV2 = inventoryItemRepository
							.findTop1ByProduct_IdAndNewVersion(productFlow.getProduct().getId(), true);

//					if (null == inventoryItem) {
//						throw new RuntimeException("Inventory Item not found:" + productFlow.getFlowReferenceId());
//					} 

					/**
					 * save NEW productFlow record to database
					 */
					Long productFlowId = (Long) hibernateSession.save(productFlow);
					productFlow.setId(productFlowId);

					/**
					 * update cash balance
					 * 
					 */
					cashBalanceService.updateCashBalance(productFlow);

					/**
					 * inventory item NEW VERSION
					 */
					int currentCount = inventoryItemV2.getCount();
					int finalCount = currentCount - productFlow.getCount();
					if (finalCount < 0) {
						// SKIP
						continue;
					}
					inventoryItemV2.setCount(finalCount);

					hibernateSession.merge(inventoryItemV2);

					purchasedProduct++;
					progressService.sendProgress(1, productFlows.size(), 30, false, requestId);
				}
			}
		}; 
		
		repositoryCustom.pesistOperation(persistenceOperation);
		transaction.setProductFlows(productFlows);

		return transaction;
	}

	public InventoryItem getInventoryByFlowRefId(long reffId) {
		return inventoryItemRepository.findByIncomingFlowId(reffId);
	}

	/**
	 * get available quantity
	 * 
	 * @param field
	 * @param value
	 * @param match
	 * @param limit
	 * @return
	 */
	public List<InventoryItem> getInventoriesByProduct(String field, Object value, boolean match, int limit) {
		String sql = "select * from inventoryitem left join product on inventoryitem.product_id = product.id "
				+ " where inventoryitem.count > 0 and product." + field + " ";
		if (match) {
			sql += " = '" + value + "'";
		} else {
			sql += " like '%" + value + "%'";
		}
		if (limit > 0) {
			sql += " limit " + limit;
		}
		return repositoryCustom.filterAndSort(sql, InventoryItem.class);
	}

	/**
	 * get current product quantity
	 * 
	 * @param product
	 * @return
	 */
	public int getProductInventory(Product product) {
		int quantity = 0;

		InventoryItem inventoryItem = inventoryItemRepository.findTop1ByProduct_IdAndNewVersion(product.getId(), true);

		if (null != inventoryItem) {
			quantity = inventoryItem.getCount();
		}

		return quantity;
	}

	public void addNewProduct(Product product) {
		log.info("Add new product: {}", product.getName());

		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setProduct(product);
		entityRepository.save(inventoryItem);
	}

}
