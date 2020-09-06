package com.fajar.shoppingmart.tests;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class REST {
	
	static RestTemplate restTemplate  = new RestTemplate();

	public static void main(String[] args) {
		ResponseEntity<String> result = restTemplate.getForEntity("http://hdpopcorns.org", String.class);
		System.out.println(result.getBody());
	}
}
