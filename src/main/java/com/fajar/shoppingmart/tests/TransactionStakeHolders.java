package com.fajar.shoppingmart.tests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.Supplier;

public class TransactionStakeHolders {
	
	private static final List<Product> products = createProductIdAndPriceMap();
	private static final List<Customer> customers = createCustomerMap(RawTransactionObjects.CUSTOMER_IDs, Customer.class);
	private static final List<Supplier> suppliers = createCustomerMap(RawTransactionObjects.SUPPLIER_IDs, Supplier.class);
	private static final Random random = new Random();
	
	private static final String APP_ID = "81750081513302535"; 
	private static final String LOGIN_KEY = "bb7a9f43-366a-485d-a693-6d9d5db5a096-2855336002";
	
	public static <T> T randomObject(List<T> list) {
		int size = list.size();
		int index = random.nextInt(size-1);
		return list.get(index);
	}
	
	public static Customer randomCustomer() { 	return randomObject(customers); }
	
	public static Supplier randomSupplier() {  	return randomObject(suppliers); }
	
	public static Product randomProduct() { 	return randomObject(products); 	}
	
	public static List<Product> randomProducts(int count){
		int productCount = random.nextInt(count)+1; 
		List<Product> list = new ArrayList<>();
		for (int i = 0; i < productCount; i++) {
			list.add(randomProduct());
		}
		return list;
	}
	
	
	public static Date randomDate(int m, int y) {
		
		int randDate = random.nextInt(25)+1;
		int randMonth = random.nextInt(11); 
		int randYear = random.nextInt(4)+2016;
		
		Calendar cal = Calendar.getInstance();
		cal.set(y, m, randDate);
		
		return cal.getTime();
	}
	
	private static List<Product> createProductIdAndPriceMap() {
		List<Product> idAndPriceMap = new ArrayList<>();
		String[] raws = RawTransactionObjects.ID_AND_PRICES.split("\r\n");
		for (int i = 0; i < raws.length; i++) {
			String raw = raws[i];
			String[] keyValue = raw.split("	");
			long id = Long.valueOf(keyValue[0]);
			long price = Long.valueOf(keyValue[1]);
			Product p =new Product();
			p.setId(id);
			p.setPrice(price);
			idAndPriceMap.add(p);
		}
		return idAndPriceMap;
	}
	
	private static <T extends  BaseEntity> List<T> createCustomerMap(long[] ids, Class<T> _class) { 
		List<T> result = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			try {
				T t = _class.newInstance();
				t.setId(ids[i]); 
				result.add(t);
			}catch (Exception e) { }
		}
		return result ;
	}
 
	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			System.out.println(i);
			randomCustomer();
			randomProduct();
			randomSupplier();
		}
	}

	public static String getApplicationID() {
		return APP_ID;
	}

	public static String getLoginKey() {			
		return LOGIN_KEY;
	}
}
