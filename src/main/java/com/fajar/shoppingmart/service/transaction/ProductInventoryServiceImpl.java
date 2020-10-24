package com.fajar.shoppingmart.service.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.TransactionMode;
import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.InventoryItem;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.CustomRepositoryImpl;
import com.fajar.shoppingmart.repository.DatabaseProcessor;
import com.fajar.shoppingmart.repository.EntityRepository;
import com.fajar.shoppingmart.repository.InventoryItemRepository;
import com.fajar.shoppingmart.repository.PersistenceOperation;
//import com.fajar.shoppingmart.repository.RepositoryCustomImpl;
import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.financial.CashBalanceService;
import com.fajar.shoppingmart.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductInventoryServiceImpl implements ProductInventoryService{

	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private CashBalanceService cashBalanceService;
	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private CustomRepositoryImpl customRepository; 
	 
	@Override
	public synchronized Transaction savePurchasingTransaction(List<ProductFlow> productFlows, User user, Supplier supplier, TransactionMode mode) {
		final Transaction transaction = buildTransactionObject(PURCHASING, user, null, supplier, mode);
		final String requestId = user.getRequestId();
		DatabaseProcessor databaseProcessor = customRepository.createDatabaseProcessor();
		databaseProcessor.keepTransaction();
		PersistenceOperation<?> op = getTransactionPersistenceOperation(transaction, productFlows, requestId, databaseProcessor);

		databaseProcessor.pesistOperation(op);
		databaseProcessor.close();
		transaction.setProductFlows(productFlows);
		return transaction;

	}
 
	@Override
	public synchronized Transaction saveSellingTransaction( List<ProductFlow> productFlows,
			User user, Customer customer, TransactionMode mode) {
		if (productFlows == null || productFlows.size() == 0) {
			throw new RuntimeException("INVALID PRODUCTS");
		}
		final String requestId = user.getRequestId();
		final Transaction transaction = buildTransactionObject(SELLING, user, customer, null, mode);
		DatabaseProcessor databaseProcessor = customRepository.createDatabaseProcessor();
		databaseProcessor.keepTransaction();
		PersistenceOperation<?> op = getTransactionPersistenceOperation(transaction, productFlows, requestId, databaseProcessor);

		databaseProcessor.pesistOperation(op);
		databaseProcessor.close();
		transaction.setProductFlows(productFlows);

		return transaction;
	}
 
 

	@Override
	public List<InventoryItem> getInventoriesByProduct(String fieldName, Object value, boolean match, int limit) {
		 
		DatabaseProcessor databaseProcessor = customRepository.createDatabaseProcessor();
		
		List<InventoryItem> result  = databaseProcessor.pesistOperation(new PersistenceOperation<List<InventoryItem>>() {

			@Override
			public List<InventoryItem> doPersist(Session hibernateSession) {
				Criteria criteria = hibernateSession.createCriteria(InventoryItem.class, "inventoryItem");
				criteria.createAlias("product", "p");
				Criterion[] filter = new Criterion[2];
				
				filter[0] = Restrictions.gt("count", 0);
				if (match) {
					filter[1] = Restrictions.eq("p."+fieldName, value);
				} else {
					filter[1] = Restrictions.like("p."+fieldName, value);
				}
				if (limit > 0) {
					criteria.setMaxResults(limit);				
				}
				
				criteria.add(Restrictions.and(filter ));
//				log.info("_______SQL_HIBERNATE_BUILT: {}", repositoryCustom.getWhereQuery(criteria));
				
				return criteria.list();
			}
			
		}); 
		
		return result;// databaseProcessor.filterAndSort(sql, InventoryItem.class);
	}

	@Override
	public int getProductInventory(Product product) {
		int quantity = 0;

		InventoryItem inventoryItem = inventoryItemRepository.findTop1ByProduct_IdAndNewVersion(product.getId(), true);

		if (null != inventoryItem) {
			quantity = inventoryItem.getCount();
		}

		return quantity;
	}

	@Override
	public void addNewProduct(Product product) {
		log.info("Add new product: {}", product.getName());

		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setProduct(product);
		entityRepository.save(inventoryItem);
	}
	
	/**
	 * create common transaction object
	 */
	private Transaction buildTransactionObject(TransactionType type, User user, Customer customer, Supplier supplier,
			TransactionMode mode) {
		Transaction transaction = new Transaction();
		transaction.setCode(StringUtil.generateRandomNumber(10));
		transaction.setUser(user);
		transaction.setType(type);
		if(user.getProcessingDate() != null) {
			transaction.setTransactionDate(user.getProcessingDate());
			transaction.setCreatedDate(user.getProcessingDate());
		} else {
			transaction.setTransactionDate(new Date()); 
		}
		transaction.setMode(mode);

		if (PURCHASING.equals(type)) {
			transaction.setSupplier(supplier);
		} else if (SELLING.equals(type)) {
			transaction.setCustomer(customer);
		}

		return transaction;
	} 
	
	private PersistenceOperation<Transaction> getTransactionPersistenceOperation(final Transaction transaction,
			List<ProductFlow> productFlows, String requestId, final DatabaseProcessor databaseProcessor) {
		log.info("Persistence Operation For trasaction type: {}, code: {}", transaction.getType(),
				transaction.getCode());

		return new PersistenceOperation<Transaction>() {

			@Override
			public Transaction doPersist(Session hibernateSession) {

				DatabaseProcessor.saveNewRecord(transaction, hibernateSession);
				progressService.sendProgress(1, 1, 10, false, requestId);

				List<ProductFlow> savedProductFlows = saveProductFlows(productFlows, transaction, requestId, databaseProcessor);
				saveNewInventoryRecords(savedProductFlows, transaction, hibernateSession);
				updateInventoryRecords(savedProductFlows, transaction, requestId, hibernateSession);

				databaseProcessor.notKeepingTransaction();
				return transaction;
			}
		};
	}

	/**
	 * update inventory count
	 * 
	 * @param savedProductFlows
	 * @param requestId
	 */
	private void updateInventoryRecords(List<ProductFlow> savedProductFlows, Transaction transaction, String requestId, Session hibernateSession) {
		final TransactionType transactionType = transaction.getType();
		log.info("update inentory item for: TRX {}", transactionType);

		for (ProductFlow productFlow : savedProductFlows) {

			boolean updateSuccess;
			try{
				updateSuccess = updateInventoryRecord(transactionType, productFlow, hibernateSession);
			}catch (Exception e) {
				updateSuccess = false;
			} 
			log.debug("updateSuccess: {}", updateSuccess);
			progressService.sendProgress(1, savedProductFlows.size(), 30, false, requestId); 
		}
	}

	/**
	 * update one inventory record
	 * @param transactionType
	 * @param productFlow
	 * @return
	 */
	private boolean updateInventoryRecord(TransactionType transactionType, ProductFlow productFlow, Session hibernateSession) { 
		 
		long productId = productFlow.getProduct().getId();
		boolean newRecord = false;
		InventoryItem inventoryItemV2 = inventoryItemRepository.findTop1ByProduct_IdAndNewVersion(productId, true);

		log.debug("inventoryItemV2, productID: {} => : {}", productId,
				inventoryItemV2 == null ? "NULL" : inventoryItemV2.getId());

		if (transactionType.equals(SELLING)) {
			 
			if (!inventoryItemV2.hasEnoughStock(productFlow)) {
				log.debug("stock of {} insufficient!", productFlow.getProduct().getName());
				return false;
			}
			inventoryItemV2.takeProduct(productFlow.getCount());// .setCount(finalCount);

		} else if (transactionType.equals(PURCHASING)) {
			
			if (null == inventoryItemV2) {
				log.info("add new record of inventoryItemV2");
				inventoryItemV2 = new InventoryItem(productFlow.getProduct(), productFlow.getCount());
				inventoryItemV2.setNewVersion(true);
				newRecord = true;
			} else { 
				log.info("productFlow count: {}", productFlow.getCount()); 
				inventoryItemV2.addProduct(productFlow.getCount()); 
			}

		}
		if(newRecord) {
			Object insertedID = hibernateSession.save(inventoryItemV2);
			log.info("insertedID: {}", insertedID);
		}else {
			hibernateSession.merge(inventoryItemV2);
		}
//		InventoryItem ret = inventoryItemRepository.save(inventoryItemV2);// repositoryCustom.saveObject(inventoryItemV2);
//		log.debug("Update inventory item: {}, count: {}", ret.getId(), ret.getCount());
		return true;
	}

	/**
	 * add new inventory item row if new products come from supplier
	 * 
	 * @param savedProductFlows
	 */
	private void saveNewInventoryRecords(List<ProductFlow> savedProductFlows, Transaction transaction, Session hibernateSession) {
		if (transaction.getType().equals(SELLING)) {
			return;
		}

		for (ProductFlow productFlow : savedProductFlows) {

			// INSERT new inventory item row
			InventoryItem inventoryItem = InventoryItem.createAndAddNewProduct(productFlow);
			hibernateSession.save(inventoryItem);
		}
	}

	private List<ProductFlow> saveProductFlows(List<ProductFlow> productFlows, final Transaction transaction,
			String requestId, DatabaseProcessor databaseProcessor) {

		log.info("saveProductFlowsAndUpdateCashBalance, count: {}", productFlows.size());
		List<ProductFlow> savedProductFlows = new ArrayList<ProductFlow>();
		List<ProductFlow> skippedProductFlows = new ArrayList<>();

		for (int i = 0; i < productFlows.size(); i++) {

			final ProductFlow productFlow = productFlows.get(i);
			if (productFlow.getCount() <= 0) {
				log.debug("Skipped Product Flow: productFlow.getCount() <= 0");
				skippedProductFlows.add(productFlow);
				continue;
			}

			productFlow.setId(null);
			productFlow.setTransaction(transaction);
			Product product = productFlow.getProduct();

			if (transaction.getType().equals(SELLING)) {
				InventoryItem inventoryItemV2 = inventoryItemRepository.findTop1ByProduct_IdAndNewVersion(product.getId(), NEW_VERSION);

				if (null == inventoryItemV2 || inventoryItemV2.getCount() < productFlow.getCount()) {
					skippedProductFlows.add(productFlow);
					continue;
				}
				if(transaction.getMode().equals(TransactionMode.REGULAR)) {
					//price by product
					productFlow.setPrice(productFlow.getProduct().getPrice());
					
				} else if (transaction.getMode().equals(TransactionMode.RETURN)) {
					//set price manually
				}
				
			}
			// save
			ProductFlow savedProductFlow = databaseProcessor.saveObject(productFlow);

			// update cash balance
			cashBalanceService.updateCashBalance(savedProductFlow);
			savedProductFlows.add(savedProductFlow);

			progressService.sendProgress(1, productFlows.size(), 30, false, requestId);
		}
		log.info("Saved Product, count: {}", savedProductFlows.size());
		log.info("Skipped Product, count: {}", skippedProductFlows.size());
		return savedProductFlows;
	} 
	
	private InventoryItem getInventoryByFlowRefId(long reffId) {
		return inventoryItemRepository.findByIncomingFlowId(reffId);
	}

	

}
