package com.fajar.shoppingmart.service.mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

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
import com.fajar.shoppingmart.repository.InventoryItemRepository;
import com.fajar.shoppingmart.repository.ProductFlowRepository;
import com.fajar.shoppingmart.repository.ProductRepository;
import com.fajar.shoppingmart.repository.SupplierRepository;
import com.fajar.shoppingmart.repository.TransactionRepository;
import com.fajar.shoppingmart.util.DateUtil;
import com.fajar.shoppingmart.util.StringUtil;
import com.fajar.shoppingmart.util.ThreadUtil;

@Service
public class RandomDataService {

	@Autowired
	private ProductFlowRepository productFlowRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private SupplierRepository supplierRepository;
	
	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	static final String insertTrx = "INSERT INTO goodshop.`transaction` "
			+ "(id, created_date, deleted, transaction_date, `type`, customer_id, user_id, supplier_id,  code) "
			+ "VALUES($ID, '$YYYY-$MM-$DD $h:$m:50.000', 0, '$YYYY-$MM-$DD $h:$m:50.000', 'PURCHASING', NULL, 1, $SUPPLIER_ID,  '$CODE'); "
			+ "";

	static final String insertFlow = "INSERT INTO goodshop.product_flow "
			+ "(  created_date, deleted, modified_date, count, expiry_date, flow_ref_id, price, transaction_id, product_id) "
			+ "VALUES(  '$YYYY-$MM-$DD 17:00:00.000', 0, NULL, $COUNT, '$YYYY-$MM-$DD 17:00:00.000', NULL, $PRICE, $TRX_ID, $PRODUCT_ID); "
			+ "";

	@PostConstruct
	public void postConstruct() {
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				addSellingTransaction();
//				validateInventory();
//				updateProductFlow();
//				checkProductFlow();
				System.out.println("=========== end post construct random data service ===========");
			}

			
		});
	}
	
	private void checkProductFlow() {

		List<Product> allProducts = productRepository.findAll();
		List<ProductFlow> productFlows = new ArrayList<>();
		
		for (Product product : allProducts) {
			
			try {
			int bought = productFlowRepository.getCount("IN", product.getId());
			int sold = productFlowRepository.getCount("OUT", product.getId());
			
			if(sold > bought) {
				int count = sold - bought;
				System.out.println("SOLD:"+sold+", bought: "+bought);
				
				ProductFlow productFlow = new ProductFlow();
				productFlow.setCount(count);
				productFlow.setPrice(product.getPrice() * 2/ 3);
				productFlow.setProduct(product);
				
				productFlows.add(productFlow );
			}
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("SKIP PRODUCT: "+product.getId());
				e.printStackTrace();
			}
			
		}
		
		
		
	}
	
	private void addTransaction(List<ProductFlow> productFlows) {
		Transaction transaction = new Transaction();
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 0, 1);
		
		transaction.setTransactionDate(cal.getTime());
		transaction.setCreatedDate(cal.getTime());
		transaction.setCode("90000000002");
		transaction.setType(TransactionType.PURCHASING);
		
		Supplier supplier = supplierRepository.findById(1L).get();
		transaction.setSupplier(supplier );
		
		transactionRepository.save(transaction);
		
		for (ProductFlow pf : productFlows) {
			pf.setExpiryDate(DateUtil.getDate(2022, 2, 23));
			pf.setTransaction(transaction);
			pf.setTransactionId(transaction.getId());
			productFlowRepository.save(pf);
		}
	}
	
	private void updateProductFlow() {
		List<ProductFlow> productFlows = productFlowRepository.findByTransaction_Id(66307L);
		for (ProductFlow productFlow : productFlows) {
			productFlow.setCreatedDate(productFlow.getTransaction().getTransactionDate()); 
			
			productFlowRepository.save(productFlow);
		}
	}
	
	private void validateInventory() {
		System.out.println("============ validate inventory ============");
		
		List<InventoryItem> invntories = inventoryItemRepository.findAll();
		Transaction transaction = new Transaction();
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 0, 1);
		
		transaction.setTransactionDate(cal.getTime());
		transaction.setCreatedDate(cal.getTime());
		
		Supplier supplier = supplierRepository.findById(1L).get();
		transaction.setSupplier(supplier );
		
		transactionRepository.save(transaction);
		
		for (InventoryItem inventoryItem : invntories) {
			if(inventoryItem.getCount() >= 0) {
				continue;
			}
			int count = Math.abs(inventoryItem.getCount());
			long price = inventoryItem.getProduct().getPrice() * 2/3;
			ProductFlow pf = new ProductFlow();
			pf.setProduct(inventoryItem.getProduct());
			pf.setCount(count);
			pf.setPrice(price);
			pf.setExpiryDate(DateUtil.getDate(2022, 2, 23));
			pf.setTransaction(transaction);
			pf.setTransactionId(transaction.getId());
			
			inventoryItem.addProduct(count);
			
			inventoryItemRepository.save(inventoryItem); 
			productFlowRepository.save(pf);
			
			System.out.println("COUNT:"+count);
		}
	}

	public void addSellingTransaction() {
		System.out.println("---------BEGIN Random Data Service---------");
		Random rand = new Random();
		List<ProductFlow> productFlows = productFlowRepository.findByTransaction_TypeAndTransaction_IdGreaterThan("IN", 645L );
		int flowSize = productFlows.size();
		for (Integer i = 1; i <= 6000; i++) {
			Integer flowCount = rand.nextInt(50) + 1;
			System.out.println("#..TRX ID: "+i+", count: "+flowCount);
			Transaction trx = new Transaction();
			
			trx.setType(TransactionType.SELLING);
			trx.setCode(StringUtil.generateRandomNumber(12));
			trx.setDeleted(false);
			Customer customer = new Customer();
			customer.setId(1L);
			trx.setCustomer(customer );
			User user = new User();
			user.setId(1L);
			trx.setUser(user );
			Date trxDate = new Date();
			List<ProductFlow> newFLows = new ArrayList<ProductFlow>();
			innerloop:	for (int j = 1; j <= flowCount; j++) {
				int productFlowIdx = rand.nextInt(flowSize-1)+1;
				int qty = rand.nextInt(9)+1;
				ProductFlow refFlow = productFlows.get(productFlowIdx);
				if(productFlows.get(productFlowIdx).getCount()-qty < 0) {
					continue innerloop;
				}
				productFlows.get(productFlowIdx).setCount(productFlows.get(productFlowIdx).getCount()-qty);
				ProductFlow pf = new ProductFlow();
				pf.setProduct(refFlow.getProduct());
				pf.setCount(qty);
				pf.setPrice(refFlow.getProduct().getPrice());
				pf.setExpiryDate(refFlow.getExpiryDate());
				 
				pf.setFlowReferenceId(refFlow.getId());
				if(refFlow.getTransaction().getTransactionDate().compareTo(trxDate)<=0) {
					trxDate = refFlow.getTransaction().getTransactionDate();
				}
				newFLows.add(pf);
			}
			trx.setTransactionDate(trxDate);
			trx.setCreatedDate(trxDate);
			trx = transactionRepository.save(trx);
			for (ProductFlow productFlow : newFLows) {
				System.out.println("--"+productFlow.getProduct().getName());
				
				
				ThreadUtil.run(new Runnable() {
					
					@Override
					public void run() { 
						InventoryItem inventoryItem = inventoryItemRepository.findTop1ByProduct_IdAndNewVersion(productFlow.getProduct().getId(), true);

						inventoryItem.takeProduct(productFlow.getCount());
						inventoryItemRepository.save(inventoryItem);
					}
				});
				
				productFlow.setTransaction(trx);
				productFlow.setTransactionId(trx.getId());
				productFlowRepository.save(productFlow);
			}
		}
	}
	
	public static String toIntStr(Integer intt) {
		return intt >= 10 ? intt.toString() : "0" + intt;
	}

	public static void main(String[] args) {
		Random rand = new Random();
		for (Integer i = 645; i <= 1645; i++) {
			Integer flowCount = rand.nextInt(50) + 1;
			System.out.println("#..TRX ID: "+i+", count: "+flowCount);
			for (int j = 1; j <= flowCount; j++) {
				Integer yyyy = rand.nextInt(3) + 2020;
				Integer prod = rand.nextInt(80) + 1;
				String mm = toIntStr(rand.nextInt(11) + 1);
				String dd = toIntStr(rand.nextInt(27) + 1);
 
				String sql = insertFlow.replace("$TRX_ID", i.toString()).replace("$MM", mm)
						.replace("$YYYY", yyyy.toString()).replace("$DD", dd).replace("$PRODUCT_ID", prod.toString())
						.replace("$PRICE", rand.nextInt(10000000) + 1000 + "")
						.replace("$COUNT", rand.nextInt(9999) + 1 + "");

				System.out.println(sql);
			}
			System.out.println("#...end");
		}
	}

	public static void mains(String[] args) {
		Random rand = new Random();
		for (Integer i = 645; i <= 1645; i++) {
			Integer yyyy = rand.nextInt(3) + 2017;
			Integer supId = rand.nextInt(100) + 1;
			String mm = toIntStr(rand.nextInt(11) + 1);
			String dd = toIntStr(rand.nextInt(27) + 1);
			String h = toIntStr(rand.nextInt(23) + 1);
			String m = toIntStr(rand.nextInt(59) + 1);
			String randomCode = StringUtil.generateRandomNumber(12);
			String sql = insertTrx.replace("$ID", i.toString()).replace("$MM", mm).replace("$YYYY", yyyy.toString())
					.replace("$DD", dd).replace("$h", h).replace("$m", m).replace("$SUPPLIER_ID", supId.toString())
					.replace("$CODE", randomCode);
			System.out.println(sql);
		}
	}
}
