package com.fajar.shoppingmart.service.financial;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.service.LogProxyFactory;

@Service
public class VoucherService {

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
}
