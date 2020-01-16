package com.fajar.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.Customer;
import com.fajar.entity.InventoryItem;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.User;
import com.fajar.repository.InventoryItemRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RepositoryCustom;
import com.fajar.repository.TransactionRepository;
import com.fajar.util.StringUtil;

@Service
public class ProductInventoryService {

	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private ProgressService progressService; 

	public Transaction saveSupplyTransaction(List<ProductFlow> productFlows, String requestId, User user,
			Supplier supplier, Date d) {
		try {
			Transaction transaction = new Transaction();
			transaction.setCode(StringUtil.generateRandomNumber(10));
			transaction.setUser(user);
			transaction.setType("IN");
			transaction.setTransactionDate(d);
			transaction.setSupplier(supplier);

			Transaction dbTransaction = transactionRepository.save(transaction);
			progressService.sendProgress(1, 1, 10, false, requestId);

			for (ProductFlow productFlow : productFlows) {

				productFlow.setId(null);// never update
				// IMPORTANT!!
				productFlow.setTransaction(dbTransaction);
				productFlow = productFlowRepository.save(productFlow);

				InventoryItem inventoryItem = new InventoryItem();
				inventoryItem.setProduct(productFlow.getProduct());
				inventoryItem.setCount(productFlow.getCount());
				inventoryItem.setOriginalCount(productFlow.getCount());
				inventoryItem.setId(productFlow.getId());
				inventoryItem.setIncomingFlowId(productFlow.getId());
				inventoryItemRepository.save(inventoryItem);

				progressService.sendProgress(1, productFlows.size(), 40, false, requestId);
			}

			dbTransaction.setProductFlows(productFlows);
			return dbTransaction;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {

		}
	}

	public synchronized Transaction savePurchaseTransaction(Date d, List<ProductFlow> productFlows, String requestId,
			User user, Customer customer) {
		if (productFlows == null || productFlows.size() == 0) {
			throw new RuntimeException("INVALID PRODUCTS");
		}

		Transaction transaction = new Transaction();
		transaction.setCode(StringUtil.generateRandomNumber(10));
		transaction.setUser(user);
		transaction.setTransactionDate(d);
		transaction.setType("OUT");
		transaction.setCustomer(customer);
		try {
			Transaction newTransaction = transactionRepository.save(transaction);
			progressService.sendProgress(1, 1, 10, false, requestId);
			int purchasedProduct = 0;
			for (ProductFlow productFlow : productFlows) {
				if (productFlow.getCount() <= 0)
					continue;
				// IMPORTANT!!
				productFlow.setTransaction(newTransaction);
				productFlow.setPrice(productFlow.getProduct().getPrice());

				InventoryItem inventoryItem = inventoryItemRepository
						.findByIncomingFlowId(productFlow.getFlowReferenceId());
				if (null == inventoryItem) {
					throw new RuntimeException("Inventory Item not found:" + productFlow.getFlowReferenceId());
				}
				/**
				 * update count
				 */
				inventoryItem.setCount(inventoryItem.getCount() - productFlow.getCount());
				if (inventoryItem.getCount() < 0) {
					continue;
				}
				inventoryItemRepository.save(inventoryItem);
				productFlow = productFlowRepository.save(productFlow);
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

	public InventoryItem getInventoryByFlowRefId(long reffId) {
		return inventoryItemRepository.findByIncomingFlowId(reffId);
	}
	
	/**
	 * get inventories
	 * @param field
	 * @param value
	 * @param match
	 * @param limit
	 * @return
	 */
	public List<InventoryItem> getInventoriesByProduct(String field, Object value, boolean match,int limit){
		String sql = "select * from inventoryitem left join product on inventoryitem.product_id = product.id " + 
				" where inventoryitem.count > 0 and product."+field+" ";
		if(match) {
			sql += " = '"+value+"'";
		}
		else {
			sql += " like '%"+value+"%'";
		}
		if(limit>0) {
			sql += " limit "+limit;
		}
		return inventoryItemRepository.filterAndSort(sql, InventoryItem.class);
	}
	
	 

}
