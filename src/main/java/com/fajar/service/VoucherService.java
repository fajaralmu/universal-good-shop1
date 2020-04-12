package com.fajar.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class VoucherService {

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
}
