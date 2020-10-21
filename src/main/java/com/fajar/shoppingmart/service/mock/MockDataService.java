//package com.fajar.shoppingmart.service.mock;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.fajar.shoppingmart.dto.TransactionType;
//import com.fajar.shoppingmart.entity.BaseEntity;
//import com.fajar.shoppingmart.entity.Capital;
//import com.fajar.shoppingmart.entity.CapitalFlow;
//import com.fajar.shoppingmart.entity.CashBalance;
//import com.fajar.shoppingmart.entity.CostFlow;
//import com.fajar.shoppingmart.entity.InventoryItem;
//import com.fajar.shoppingmart.entity.Product;
//import com.fajar.shoppingmart.entity.ProductFlow;
//import com.fajar.shoppingmart.entity.Transaction;
//import com.fajar.shoppingmart.repository.CapitalFlowRepository;
//import com.fajar.shoppingmart.repository.CapitalRepository;
//import com.fajar.shoppingmart.repository.CashBalanceRepository;
//import com.fajar.shoppingmart.repository.CostFlowRepository;
//import com.fajar.shoppingmart.repository.InventoryItemRepository;
//import com.fajar.shoppingmart.repository.ProductFlowRepository;
//import com.fajar.shoppingmart.repository.ProductRepository;
//import com.fajar.shoppingmart.repository.TransactionRepository;
//import com.fajar.shoppingmart.util.DateUtil;
//import com.fajar.shoppingmart.util.ThreadUtil;
//
//@Service
//public class MockDataService {
//
//	@Autowired
//	public TransactionRepository transactionRepository;
//	@Autowired
//	public ProductRepository productRepository;
//	@Autowired
//	public ProductFlowRepository productFlowRepository;
//	@Autowired
//	public InventoryItemRepository inventoryItemRepository;
//	@Autowired
//	public CapitalFlowRepository capitalFlowRepository;
//	@Autowired
//	public CapitalRepository capitalRepository;
//	@Autowired
//	public CostFlowRepository costFlowRepository;
//	@Autowired
//	public CashBalanceRepository cashBalanceRepository;
//	
//	static final Random RAND = new Random();
//
//	@PostConstruct
//	public void init() {
//		
//		System.out.println("---------------------- MOCK ----------------------");
//		ThreadUtil.run(new Runnable() {
//			
//			@Override
//			public void run() { 
//				System.out.println("*************@PostConstruct**************");
////				writeCashBalance();
////				updateProductFlows();
////				updateSoldProductFlowsPrice();
////				fixIncorrectTransactionDate();
//			}
//
//			
//
//			
//		});
//	}
//	
//	public void fixIncorrectTransactionDate() {
//		// TODO Auto-generated method stub
//	
//		List<ProductFlow> incorrectFlows = productFlowRepository.FINDINCORRECTDATE();
//		int i = 0;
//		for (ProductFlow productFlow : incorrectFlows) {
//			Transaction transaction = productFlow.getTransaction();
//			
//			transaction.setTransactionDate(productFlow.getCreatedDate());
//			transaction.setCreatedDate(productFlow.getCreatedDate());
//			transaction.setModifiedDate(productFlow.getCreatedDate());
//			transactionRepository.save(transaction);
//			System.out.println("saved: "+transaction.getId()+"index:"+i+" of "+incorrectFlows.size());
//			i++;
//		}
//	}
//	
//	public void updateSoldProductFlowsPrice() {
//		List<ProductFlow> productFlows = productFlowRepository.findByTransaction_Type(TransactionType.OUT);
//		for (ProductFlow productFlow : productFlows) {
//			
//			System.out.println("Pre price: "+productFlow.getPrice());
//			Product product = productFlow.getProduct();
//			productFlow.setPrice(product.getPrice());
//			
//			System.out.println("Updated Price: "+productFlow.getPrice());
//			productFlowRepository.save(productFlow);
//			System.out.println("Saved: "+productFlow.getId());
//		}
//		
//	}
//	public void updateAllProductFlows() {
//		List<ProductFlow> productFlows = productFlowRepository.findAll();
//		for (ProductFlow productFlow : productFlows) {
//			Transaction transaction = productFlow.getTransaction();
//			productFlow.setCreatedDate(transaction.getTransactionDate());
//			productFlowRepository.save(productFlow);
//			System.out.println("saved: "+productFlow.getId());
//		}
//	}
//	
//	public void writeCashBalance() {
//		List<BaseEntity> mainList = new ArrayList<>(); 
//		
//		List<ProductFlow> productFlows = productFlowRepository.findAll();
//		System.out.println("productFlows:"+productFlows.size());
//		List<CostFlow> costFlows = costFlowRepository.findAll();
//		System.out.println("costFlows:"+costFlows.size());
//		List<CapitalFlow> capitalFlows = capitalFlowRepository.findAll();
//		System.out.println("capitalFlows:"+capitalFlows.size());
//		
//		mainList.addAll(capitalFlows);
//		mainList.addAll(costFlows);
//		mainList.addAll(productFlows);
////		Comparator<? super BaseEntity> comparator = new Comparator<BaseEntity>() {
////
////			@Override
////			public int compare(BaseEntity o1, BaseEntity o2) {
////				// TODO Auto-generated method stub
////				int result = 0;
////				if(o1.getCreatedDate().compareTo(o2.getCreatedDate()) < 0) {
////					result = -1;
////				}else {
////					result = 1;
////				}
////				
////				return result;
////			}
////		};
////		mainList.sort(comparator );
//		
//		CashBalance[] cashBalances = new CashBalance[mainList.size()];
//		BaseEntity[] sortedList = urutkanListObj(mainList);
//		
//		for (int i = 0 ;i< sortedList.length; i++) {
//			BaseEntity baseEntity  = sortedList[i];
//
//			CashBalance cashBalance = new CashBalance();
//			cashBalance.setDate(baseEntity.getCreatedDate()); 
//			cashBalance.setReferenceId(baseEntity.getId().toString());
//			
//			long formerBalance = i == 0? 0l : cashBalances[i-1].getActualBalance();
//			long creditAmount = 0l;
//			long debitAmount = 0l;
//			
//			String info = "";
//			
//			System.out.println(i+". date:" + baseEntity.getCreatedDate()+" | "+baseEntity.getClass());
//			if(baseEntity instanceof ProductFlow) {
//				
//				ProductFlow productFlow = (ProductFlow) baseEntity;
//				Transaction transaction = productFlow.getTransaction();
//				 
//				
//				if(transaction.getType().equals(TransactionType.IN)) {
//					creditAmount = productFlow.getCount() * productFlow.getPrice();
//				}else {
//					debitAmount = productFlow.getCount() * productFlow.getPrice();
//				}
//				
//				info = "TRAN_"+transaction.getType();
//				
//			}else if(baseEntity instanceof CostFlow) {
//				
//				CostFlow costFlow = (CostFlow) baseEntity;
//				creditAmount = costFlow.getNominal();
//				
//				info = "COST_"+costFlow.getCostType().getName();
//				
//			}else if(baseEntity instanceof CapitalFlow) {
//				
//				CapitalFlow capitalFlow = (CapitalFlow) baseEntity;
//				debitAmount = capitalFlow.getNominal();
//				
//				info = "CAPITAL_"+capitalFlow.getCapitalType().getName();
//			}
//			
//			if(i == 0) {
//				formerBalance = 0l;
//			}
//			cashBalance.setFormerBalance(formerBalance);
//			cashBalance.setDebitAmt(debitAmount);
//			cashBalance.setCreditAmt(creditAmount);
//			cashBalance.setActualBalance(formerBalance + creditAmount - debitAmount);
//			cashBalance.setReferenceInfo(info);
//			
//			cashBalances[i] = cashBalance;
//		}
//		
//		ThreadUtil.run(new Runnable() {
//			
//			@Override
//			public void run() {
//				for (int i = 0; i < cashBalances.length; i++) {
//					CashBalance dbCashBalance = cashBalanceRepository.save(cashBalances[i]);
//					System.out.println("saved: "+dbCashBalance.getId());
//				}
//				
//			}
//		});
//	}
//	
//	public static void main(String[] args0) {
//		for(int i = 0;i<100;i++) {
//			Date date  = randomDate();
//			String dateString = DateUtil.formatDate(date, "dd-MM-yyyy");
//			System.out.println(dateString);
//		}
//	}
//	
//	public void buildCapitals() {
//		List<Capital> capitals = capitalRepository.findAll();
//		for(int i = 0;i<100;i++) {
//			Date date  = randomDate();
//			 
//			CapitalFlow capitalFlow = new CapitalFlow();
//			capitalFlow.setDate(date);
//			capitalFlow.setDescription("SYSTEM TEST");
//			capitalFlow.setNominal(RAND.nextInt(50000000)+1000000); 
//			capitalFlow.setCapitalType(i%2 == 0? capitals.get(0):capitals.get(1));
//			capitalFlowRepository.save(capitalFlow);
//		}
//	}
//	
//	public void updatePurchaseTrx() {
//		System.out.println("============ BEGIN updatePurchaseTrx ============");
//		
//		final List<Product> allProducts= productRepository.findAll();
//		Thread thread = new Thread(()-> { 
//			
//			for (Product product : allProducts) {
//				System.out.println("start -> "+product);
//				
//				InventoryItem inventoryItem = inventoryItemRepository.findTop1ByProduct_IdAndNewVersion(product.getId(), true);
//				
//				int count = inventoryItem.getCount();
//				int reduce = 0;
//				List<ProductFlow> productFlows = productFlowRepository.findByTransaction_TypeAndProduct_Id(TransactionType.OUT, product.getId());
//				
//				System.out.println("Product flow count: "+productFlows.size());
//				for (ProductFlow productFlow : productFlows) {
//					reduce += productFlow.getCount();
//				}
//				System.out.println("Total products: "+count+", sold: "+reduce+" remaining: "+(count - reduce));
//				
//				int finalCount = count - reduce;
//				inventoryItem.setCount(finalCount);
//				
//				inventoryItemRepository.save(inventoryItem);
//				
//				System.out.println("end -> "+product.getName());
//			}
//			
//		});
//		thread.start();
//	}
//	
//	public void updateIncomingTrx( ) {
//		
//		System.out.println("============ BEGIN updateIncomingTrx ============");
//		
//		final List<Product> allProducts= productRepository.findAll();
//		Thread thread = new Thread(()-> { 
//			
//			for (Product product : allProducts) {
//				System.out.println("start -> "+product);
//				
//				int count = 0;
//				List<ProductFlow> productFlows = productFlowRepository.findByTransaction_TypeAndProduct_Id(TransactionType.IN, product.getId());
//				
//				System.out.println("Product flow count: "+productFlows.size());
//				for (ProductFlow productFlow : productFlows) {
//					count+=productFlow.getCount();
//				}
//				System.out.println("Total products: "+count);
//				
//				InventoryItem inventoryItem = new InventoryItem();
//				inventoryItem.setIncomingFlowId(0);
//				inventoryItem.setCount(count);
//				inventoryItem.setProduct(product);
//				inventoryItem.setNewVersion(true);
//				
//				inventoryItemRepository.save(inventoryItem);
//				
//				System.out.println("end -> "+product);
//			}
//			
//		});
//		thread.start();
//	}
//
//	public void randDate() {
//		List<Transaction> transactions = transactionRepository.findByType(TransactionType.IN);
//		
//		System.out.println("******** will begin manipulating , COUNT: "+ transactions.size() +"***********");
//		
//		Thread thread = new Thread(() -> {
//			int i = 0;
//			for (Transaction transaction : transactions) {
//				Date date = transaction.getTransactionDate();
//				
//				LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
//				localDateTime = localDateTime.plusDays(RAND.nextInt(30)+1); 
//				Date newDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
//				
//				transaction.setTransactionDate(newDate);
//				
//				transactionRepository.save(transaction);
//				i++;
//				System.out.println(i+" of "+ transactions.size()+" updated");
//			}
//		});
//
//		thread.start();
//	}
//	
//	public static Date randomDate() {
//		
//		int randDate = RAND.nextInt(25)+1;
//		int randMonth = RAND.nextInt(11); 
//		int randYear = RAND.nextInt(4)+2016;
//		
//		Calendar cal = Calendar.getInstance();
//		cal.set(randYear, randMonth, randDate);
//		
//		return cal.getTime();
//	}
//	 
//	public static BaseEntity[] urutkanListObj(List<BaseEntity> list_obj) {
//		System.out.println("WILL SORT");
//		
//		BaseEntity[] list_urut = new BaseEntity[list_obj.size()];
//
//		Map<Integer, BaseEntity> map_obj = new HashMap<Integer, BaseEntity>();
//		Map<Integer, BaseEntity> map_obj_urut = new HashMap<Integer, BaseEntity>();
//		Map<Integer, BaseEntity> map_obj_urut_final = new HashMap<Integer, BaseEntity>(); 
//
//		BaseEntity min = maxObj(list_obj);
//		int key = 0;
//		int key_map_urut = 0;
//		for (int i = 0; i < list_obj.size(); i++) {
//			map_obj.put(i, list_obj.get(i));
//			 System.out.println("moving list:"+i);
//		}
//
//		for (int i = 0; i < map_obj.size(); i++) {
//			loop: for (int j = 0; j < map_obj.size(); j++) {
//				if (map_obj_urut.containsKey(j))
//					continue;
//				
//				BaseEntity item = map_obj.get(j);
//				if (item.getCreatedDate().compareTo(  min.getCreatedDate()) <= 0) {
//					min = map_obj.get(j);
//					key = j;
//				} else {
//					continue loop;
//				} 
//			}
//			System.out.println("key:"+ key + new Date().toString());
//			map_obj_urut_final.put(key_map_urut, min);
//			map_obj_urut.put(key, min);
//			key_map_urut++;
//			// System.out.println("Added"+map_int_urut.get(key));
//			min = maxObj(list_obj);
//		}
//
//		for (int i = 0; i < map_obj_urut_final.size(); i++) {
//			list_urut[i] = map_obj_urut_final.get(i);
//		}
//
//		System.out.println("oke. New List: ");
//		/*
//		 * for (int i = 0; i < list_urut.length; i++) System.out.print(list_urut[i] +
//		 * ","); System.out.println("--");
//		 */
//		return list_urut;
//	}
//	
//	public static BaseEntity maxObj(List<BaseEntity> list) {
//		BaseEntity max = list.get(0);
//		for (BaseEntity object : list) {
//			
//			if(object.getCreatedDate().compareTo(max.getCreatedDate())>0) {
//				max = object;
//			}
//			
//		}
//		return max;
//	}
//
//}