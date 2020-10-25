package com.fajar.shoppingmart.tests;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.util.MapUtil;
import com.fajar.shoppingmart.util.ThreadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionSellTest {
	static Random random = new Random();
	static final RestTemplate REST_TEMPLATE = new RestTemplate();
	static final String endPoint = "http://localhost:8080/universal-good-shop/api/transaction/selling";
	static final String endPointGetProduct = "http://localhost:8080/universal-good-shop/api/public/get";
	
	static ObjectMapper objectMapper = new ObjectMapper();

	static WebRequest createTransactionRequest(int m, int y) { 
		List<ProductFlow> productFlows = new ArrayList<ProductFlow>(); 
		final List<Product> products = TransactionStakeHolders.randomProducts(15);
		List<Thread> threads = new ArrayList<>();
		for (final Product product : products) {
			threads.add(ThreadUtil.run(()->{
			ProductFlow pf = createProductFlow(product);
			if(null == pf) {
				return;
			}
			productFlows.add(pf );
			}));
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(productFlows.size() == 0) {
			return null;
		}
		
		WebRequest webRequest = new WebRequest();
		webRequest.setCustomer(TransactionStakeHolders.randomCustomer());
		webRequest.setTransaction(Transaction.builder().transactionDate(TransactionStakeHolders.randomDate(m, y)).build());
		webRequest.setProductFlows(productFlows);
		return webRequest;
	}
	
	static ProductFlow createProductFlow(Product p) {
		
		ProductFlow productFlow = new ProductFlow();
		productFlow.setProduct(p);
		int stock = checkProductStock(p);
		if(stock == 0) {
			return null;
		}
		int count = random.nextInt(10)+1;
		if(count>stock) {
			count = 1;
		}
		productFlow.setCount(count);
		return productFlow ;
	}
	
	static int checkProductStock(Product p)   {
		try {
			String payloadRaw = "{\"entity\":\"product\",\"filter\":{\"limit\":1,\"exacts\":true,\"fieldsFilter\":{\"id\":"+p.getId()+",\"withStock\":true}}}";
			System.out.println("payloadRaw: "+payloadRaw);
			WebRequest payload = objectMapper.readValue(payloadRaw, WebRequest.class);
			HttpEntity<WebRequest> req = RestComponent.buildAuthRequest(payload , true);
			ResponseEntity<HashMap<Object, Object>> response = REST_TEMPLATE.postForEntity( (endPointGetProduct), req,RestComponent.getEmptyHashMapClass());
			Map<Object, Object> body = response.getBody();
			List mapList = (List) body.get("entities");
			List<Product> products = MapUtil.convertMapList(mapList , Product.class);// body.get("entities");
			return products.get(0).getCount();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return 0;
	}
	
	static void doTransaction(int month, int year, int index) {
		WebRequest payload = createTransactionRequest(month, year);
		if(null == payload) {
			System.out.println("=========Skip Transaction Index:"+index);
			return;
		}
		HttpEntity<WebRequest> req = RestComponent.buildAuthRequest(payload , true);
		ResponseEntity<WebResponse> response = REST_TEMPLATE.postForEntity( (endPoint), req, WebResponse.class);
		System.out.println("index:"+index+"   "+response.getBody());
	}
	
	public static void main(String[] args) {
		randomTrx();
	}
	
	static void randomTrx() {
		for (int i = 0; i < 200; i++) {
			final int seq = i;
			//ThreadUtil.run(()->{
			//System.out.println("SEQUENCE: "+seq);
			doTransaction(3, 2016, seq);
			//});
		}
	}
}
