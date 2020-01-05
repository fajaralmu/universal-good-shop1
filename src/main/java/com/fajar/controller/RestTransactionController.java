package com.fajar.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.service.UserAccountService;
import com.fajar.service.UserSessionService;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.ProductService;
import com.fajar.service.TransactionService;

@CrossOrigin
@RestController
@RequestMapping("/api/transaction")
public class RestTransactionController {
	Logger log = LoggerFactory.getLogger(RestTransactionController.class);
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ProductService productService;

	public RestTransactionController() {
		log.info("------------------RestTransactionController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	@PostMapping(value = "/supply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse addSupply(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("supply {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.supplyProduct(request, httpRequest,httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/purchase", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse purchase(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("purchase {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.addPurchaseTransaction(request, httpRequest,httpRequest.getHeader("requestId"));
		return response;
	}
	@PostMapping(value = "/stocks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse stockinfo(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("stocks {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.getStocksByProductName(request, false, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/stockinfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse stockInfo(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("stockinfo {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.stockInfo(request);
		return response;
	}
	
	@PostMapping(value = "/cashflowinfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse cashflowinfo(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("cashflowinfo {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.getCashFlow(request);
		return response;
	}
	
	@PostMapping(value = "/cashflowdetail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse detailcashflow(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("cashflowdetail {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = transactionService.getCashflowDetail(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/productsales", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse productsales(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("productsales {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = productService.getProductSales(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/productsalesdetail/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse productsalesdetail(@PathVariable(required = true, name="id") Long productId,@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("productsales {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = productService.getProductSalesDetail(request,productId, httpRequest.getHeader("requestId"));
		return response;
	}
	
	

}
