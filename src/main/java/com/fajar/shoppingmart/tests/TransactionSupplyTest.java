package com.fajar.shoppingmart.tests;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.util.ThreadUtil;

public class TransactionSupplyTest {
	static Random random = new Random();
	static final RestTemplate REST_TEMPLATE = new RestTemplate();
	static final String endPoint = "http://localhost:8080/universal-good-shop/api/transaction/purchasing";

	static WebRequest createTransactionRequest(int m, int y) { 
		List<ProductFlow> productFlows = new ArrayList<ProductFlow>(); 
		List<Product> products = TransactionStakeHolders.randomProducts(50);
		for (Product product : products) {
			productFlows.add(createProductFlow(product));
		}
		
		WebRequest webRequest = new WebRequest();
		webRequest.setSupplier(TransactionStakeHolders.randomSupplier());
		webRequest.setTransaction(Transaction.builder().transactionDate(TransactionStakeHolders.randomDate(m, y)).build());
		webRequest.setProductFlows(productFlows);
		return webRequest;
	}
	
	static ProductFlow createProductFlow(Product p) {
		
		ProductFlow productFlow = new ProductFlow();
		productFlow.setProduct(p);
		productFlow.setPrice(p.getPrice()*((Double)(80.d/100.d)).longValue());
		productFlow.setCount(random.nextInt(100)+1);
		return productFlow ;
	}
	
	static void doTransaction(int month, int year, int index) {
		HttpEntity<WebRequest> req = RestComponent.buildAuthRequest(createTransactionRequest(month, year), true);
		ResponseEntity<WebResponse> response = REST_TEMPLATE.postForEntity( (endPoint), req, WebResponse.class);
		System.out.println("index:"+index+response.getBody());
	}
	
	public static void main(String[] args) {
		System.out.println(TransactionStakeHolders.randomProduct().getPrice()*(80.d/100.d));
	}
	
	static void randomTrx() {
		for (int i = 0; i < 100; i++) {
			final int seq = i;
			ThreadUtil.run(()->{
				System.out.println("SEQUENCE: "+seq);
				doTransaction(10, 2020, seq);
			});
		}
	}

}
