package com.fajar.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.service.AccountService;
import com.fajar.service.TransactionService;

@CrossOrigin
@RestController
@RequestMapping("/api/transaction")
public class RestTransactionController {
	Logger log = LoggerFactory.getLogger(RestTransactionController.class);
	@Autowired
	private AccountService accountService;
	@Autowired
	private TransactionService transactionService;

	public RestTransactionController() {
		log.info("------------------RestTransactionController-----------------");
	}

	@PostMapping(value = "/supply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse addSupply(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("supply {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.supplyProduct(request, httpRequest);
		return response;
	}
	
	@PostMapping(value = "/purchase", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse purchase(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("purchase {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.addPurchaseTransaction(request, httpRequest);
		return response;
	}
	@PostMapping(value = "/stocks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse stockinfo(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("stocks {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.getStocksByProductName(request, false);
		return response;
	}
	
	@PostMapping(value = "/stockinfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse stockInfo(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("stockinfo {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.stockInfo(request);
		return response;
	}
	
	@PostMapping(value = "/cashflowinfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse cashflowinfo(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("cashflowinfo {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.getCashFlow(request);
		return response;
	}
	
	@PostMapping(value = "/cashflowdetail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse detailcashflow(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("cashflowdetail {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.getCashflowDetail(request);
		return response;
	}
	
	

}
