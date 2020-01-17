//package com.fajar.service; 
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Optional;
//import java.util.Random;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.fajar.entity.Customer;
//import com.fajar.entity.InventoryItem;
//import com.fajar.entity.Product;
//import com.fajar.entity.ProductFlow;
//import com.fajar.entity.Supplier;
//import com.fajar.entity.Transaction;
//import com.fajar.entity.User;
//import com.fajar.repository.CustomerRepository;
//import com.fajar.repository.InventoryItemRepository;
//import com.fajar.repository.ProductFlowRepository;
//import com.fajar.repository.ProductRepository;
//import com.fajar.repository.SupplierRepository;
//import com.fajar.repository.TransactionRepository;
//import com.fajar.repository.UserRepository;
//
//@Service
//public class MockDataService {
//
//	@Autowired
//	private SupplierRepository supplierRepository;
//	@Autowired
//	private ProductRepository productRepository;
//	@Autowired
//	private CustomerRepository customerRepository;
//	@Autowired
//	private ProductInventoryService productInventoryService;
//	@Autowired
//	private UserRepository userRepository;
//	@Autowired
//	private TransactionRepository transactionRepository;
//	@Autowired
//	private ProductFlowRepository productFlowRepository;
//	@Autowired
//	private InventoryItemRepository inventoryItemRepository;
//
//	private List<Customer> customers;
//	private List<Supplier> suppliers;
//	private List<Product> products;
//
//	private static final Random rand = new Random();
//	private User user;
//
//	@PostConstruct
//	public void init() {
//		customers = customerRepository.findAll();
//		suppliers = supplierRepository.findAll();
//		products = productRepository.findAll();
//		user = userRepository.findByUsernameAndPassword("admin123", "123");
//		if (user == null)
//			throw new RuntimeException("USER NOT FOUND");
//		System.out.println("=============================BEGINNING==========================");
//		Thread t = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				System.out.println(" o o o o RUNNING THREAD 1 o o o o ");
//				execPurchase(8176, 8500, 1);
//			}
//		});
//		Thread t2 = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				System.out.println(" o o o o RUNNING THREAD 2 o o o o ");
//				execPurchase(8500, 9000, 2);
//			}
//		});
//		Thread t3 = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				System.out.println(" o o o o RUNNING THREAD 3 o o o o ");
//				execPurchase(9000, 9500, 3);
//
//			}
//		});
//		Thread t4 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				System.out.println(" o o o o RUNNING THREAD 4o o o o ");
////				execValidate();
//				execSupply();
//
//			}
//		});
//
////		t.start();
////		t2.start();
////		t3.start();
//		
////	t4.start();
//		 
//
//	}
//	
//	public static void main(String[] args) {
//		System.out.println("RANDOM: "+rand.nextInt(2));
//	}
//	
//	void execValidate() {
//		System.out.println("=====BEGIN VALIDATION======");
//		List<Long> unregisteredId = new ArrayList<>();
//		List<ProductFlow> productFlows = productFlowRepository.findByTransaction_Type("IN");
//		for (ProductFlow productFlow : productFlows) {
//			InventoryItem inventory = inventoryItemRepository.findByIncomingFlowId(productFlow.getId());
//			if(null == inventory) {
//				System.out.println("x x x x UNREGISTERED: "+productFlow.getId());
//				unregisteredId.add(productFlow.getId());
//			}
//		}
//		System.out.println("UNREGISTERED IDs: "+unregisteredId);
//	}
//	
//	void execModification() {
//		List<InventoryItem>inventories = inventoryItemRepository.findByCountGreaterThan(0);
//		System.out.println("###SIZE: "+inventories.size());
//		for (InventoryItem inventoryItem : inventories) {
//			int number = rand.nextInt(5);
//			System.out.println("--------number:"+number);
//			if(number==1) continue;
//			System.out.println("o o INVENTORY: "+inventoryItem.getCount());
//			
//			Optional<ProductFlow> productFlow = productFlowRepository.findById(inventoryItem.getIncomingFlowId());
//			if(productFlow.isPresent() == false) {
//				continue;
//			}
//			
//			ProductFlow productFlowEntity = productFlow.get();
//			
//			final int originalCount = productFlowEntity.getCount();
//			final int inventoryRemaining = inventoryItem.getCount();
//			
//			productFlowEntity.setCount(originalCount-inventoryRemaining);
//			inventoryItem.setCount(0);
//			inventoryItem.setOriginalCount(originalCount-inventoryRemaining);
//			
//			/**
//			 * save to DB
//			 */
//			productFlowRepository.save(productFlowEntity);
//			inventoryItemRepository.save(inventoryItem);
//			System.out.println("_________________________[UPDATED]______________________");
//		}
//		System.out.println("_____________END_____________");
//	}
//
//	Customer randomCustomer() {
//		return customers.get(rand.nextInt(customers.size() - 1));
//	}
//
//	Product randomproducts() {
//		return products.get(rand.nextInt(products.size() - 1));
//	}
//
//	Supplier randomsuppliers() {
//		return suppliers.get(rand.nextInt(suppliers.size() - 1));
//	}
//
//	int randomProductFlowCount() {
//		return rand.nextInt(19) + 1;
//	}
//
//	boolean existInTheCart(long productId, List<ProductFlow> productFlows) {
//		for (ProductFlow productFlow : productFlows) {
//			if (productFlow.getProduct().getId().equals(productId))
//				return true;
//		}
//		return false;
//	}
//
//	Date randomTrxDate() {
//		Calendar cal = Calendar.getInstance();
//		cal.set(rand.nextInt(4) + 2016, rand.nextInt(11) + 1, rand.nextInt(20) + 5);
//		return cal.getTime();
//	}
//
//	Date randomExpDate() {
//		Calendar cal = Calendar.getInstance();
//		cal.set(rand.nextInt(2) + 2022, rand.nextInt(11) + 1, rand.nextInt(20) + 5);
//		return cal.getTime();
//	}
//
//	int randomCount() {
//		return rand.nextInt(299) + 1;
//	}
//
//	int randomTrxOutCount() {
//		return rand.nextInt(10) + 1;
//	}
//
//	List<ProductFlow> randomProductFlows(List<ProductFlow> productFlows) {
//
//		List<ProductFlow> flows = new ArrayList<>();
//		for (ProductFlow productFlow : productFlows) {
//
//			int decide = rand.nextInt(4);
//			if (decide == 3) {
//				continue;
//			}
//			InventoryItem inventory = productInventoryService.getInventoryByFlowRefId(productFlow.getId());
//			int bound = inventory.getOriginalCount() / 4;
//			int count = rand.nextInt(bound <= 0 ? 2 : bound) + 1;
//			if (inventory.getCount() < count || count <= 0) {
//				continue;
//			}
//
//			ProductFlow flow = new ProductFlow();
//
//			flow.setCount(count);
//			flow.setProduct(productFlow.getProduct());
//			flow.setFlowReferenceId(productFlow.getId());
//			flow.setPrice(productFlow.getProduct().getPrice());
//			flows.add(flow);
//
//		}
//		return flows;
//	}
//
//	void execPurchase(long from, long to, int thread) {
//		List<Transaction> transactions = transactionRepository.findByTypeAndIdGreaterThanAndIdLessThan("IN", from, to);
//		System.out.println("i i i i i TRX TOTAL COUNT: " + transactions.size());
//		for (Transaction transaction : transactions) {
//
//			final List<ProductFlow> productFLows = productFlowRepository.findByTransaction_Id(transaction.getId());
//			int trxCount = randomTrxOutCount();
//			int executed = 0;
//			loop2: for (int i = 0; i < trxCount; i++) {
//				try {
//					List<ProductFlow> workingFlows = randomProductFlows(productFLows);
//
//					if (workingFlows.size() == 0)
//						continue loop2;
//					Transaction newTransaction = productInventoryService.savePurchaseTransaction(
//							transaction.getTransactionDate(), workingFlows, "0001", this.user, randomCustomer());
//					System.out.println("==============================================");
//					System.out.println("(" + thread + ")========ADDED " + newTransaction.getCode() + "==========");
//					System.out.println("=======================(" + transaction.getId() + ")========================");
//					executed++;
//				} catch (Exception ex) {
//					System.out.println("[ERROR CATCHED]" + ex);
//				}
//			}
//			System.out.println("===============================================");
//			System.out.println("===============================================");
//			System.out.println("============TOTAL:" + executed + "from:" + transaction.getId() + "===============");
//			System.out.println("===============================================");
//			System.out.println("===============================================");
//		}
//		System.out.println("=============================END TREAD: " + thread);
//	}
//
//	void execSupply() {
//		for (int i = 0; i < 200; i++) {
//			System.out.println("___" + i);
//			int flowCount = randomProductFlowCount();
//			List<ProductFlow> productFlows = new ArrayList<ProductFlow>();
//			loop: for (int j = 0; j < flowCount; j++) {
//				ProductFlow pf = new ProductFlow();
//				final Product product = randomproducts();
//				if (existInTheCart(product.getId(), productFlows))
//					continue loop;
//				pf.setCount(randomCount());
//				pf.setExpiryDate(randomExpDate());
//				pf.setProduct(product);
//				pf.setPrice(product.getPrice() - (20 / 100 * product.getPrice()));
//				productFlows.add(pf);
//
//			}
//			try {
//				Transaction transaction = productInventoryService.saveSupplyTransaction(productFlows, "12345", user,
//						randomsuppliers(), randomTrxDate());
//				System.out.println("SUCCESS: " + transaction.getId());
//			} catch (Exception e) {
//				// TODO: handle exception
//				System.out.println("ERROR: " + e);
//				e.printStackTrace();
//			}
//		}
//	}
//
//}
