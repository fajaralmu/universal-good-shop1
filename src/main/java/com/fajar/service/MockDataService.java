package com.fajar.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entity.InventoryItem;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Transaction;
import com.fajar.repository.InventoryItemRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.TransactionRepository;

@Service
public class MockDataService {

	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	
	
	static final Random RAND = new Random();

	@PostConstruct
	public void init() {

//		randDate():
//		updateIncomingTrx();
//		updatePurchaseTrx();
	}
	
	private void updatePurchaseTrx() {
		System.out.println("============ BEGIN updatePurchaseTrx ============");
		
		final List<Product> allProducts= productRepository.findAll();
		Thread thread = new Thread(()-> { 
			
			for (Product product : allProducts) {
				System.out.println("start -> "+product);
				
				InventoryItem inventoryItem = inventoryItemRepository.findTop1ByProduct_IdAndNewVersion(product.getId(), true);
				
				int count = inventoryItem.getCount();
				int reduce = 0;
				List<ProductFlow> productFlows = productFlowRepository.findByTransaction_TypeAndProduct_Id("OUT", product.getId());
				
				System.out.println("Product flow count: "+productFlows.size());
				for (ProductFlow productFlow : productFlows) {
					reduce += productFlow.getCount();
				}
				System.out.println("Total products: "+count+", sold: "+reduce+" remaining: "+(count - reduce));
				
				int finalCount = count - reduce;
				inventoryItem.setCount(finalCount);
				
				inventoryItemRepository.save(inventoryItem);
				
				System.out.println("end -> "+product.getName());
			}
			
		});
		thread.start();
	}
	
	private void updateIncomingTrx( ) {
		
		System.out.println("============ BEGIN updateIncomingTrx ============");
		
		final List<Product> allProducts= productRepository.findAll();
		Thread thread = new Thread(()-> { 
			
			for (Product product : allProducts) {
				System.out.println("start -> "+product);
				
				int count = 0;
				List<ProductFlow> productFlows = productFlowRepository.findByTransaction_TypeAndProduct_Id("IN", product.getId());
				
				System.out.println("Product flow count: "+productFlows.size());
				for (ProductFlow productFlow : productFlows) {
					count+=productFlow.getCount();
				}
				System.out.println("Total products: "+count);
				
				InventoryItem inventoryItem = new InventoryItem();
				inventoryItem.setIncomingFlowId(0);
				inventoryItem.setCount(count);
				inventoryItem.setProduct(product);
				inventoryItem.setNewVersion(true);
				
				inventoryItemRepository.save(inventoryItem);
				
				System.out.println("end -> "+product);
			}
			
		});
		thread.start();
	}

	private void randDate() {
		List<Transaction> transactions = transactionRepository.findByType("OUT");
		
		System.out.println("******** will begin manipulating , COUNT: "+ transactions.size() +"***********");
		
		Thread thread = new Thread(() -> {
			int i = 0;
			for (Transaction transaction : transactions) {
				Date date = transaction.getTransactionDate();
				
				LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
				localDateTime = localDateTime.plusDays(RAND.nextInt(30)+1); 
				Date newDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
				
				transaction.setTransactionDate(newDate);
				
				transactionRepository.save(transaction);
				i++;
				System.out.println(i+" of "+ transactions.size()+" updated");
			}
		});

		thread.start();
	}
}