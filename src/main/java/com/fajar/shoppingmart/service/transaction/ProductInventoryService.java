package com.fajar.shoppingmart.service.transaction;

import java.util.List;

import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.InventoryItem;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.User;

public interface ProductInventoryService {

	public static final TransactionType TYPE_OUT = TransactionType.OUT;
	public static final TransactionType TYPE_IN = TransactionType.IN;
	public static final boolean NEW_VERSION = Boolean.TRUE;
	
	public int getProductInventory(Product product);

	public void refreshSessions();

	public Transaction savePurchasingTransaction(List<ProductFlow> productFlows, User user, Supplier supplier);

	public List<InventoryItem> getInventoriesByProduct(String key, Object value, boolean match, int limit);

	public Transaction saveSellingTransaction(List<ProductFlow> productFlows, User user, Customer customer);

	public void addNewProduct(Product newProduct);

}
