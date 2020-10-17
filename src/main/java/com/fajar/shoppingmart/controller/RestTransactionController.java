package com.fajar.shoppingmart.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.annotation.CustomRequestInfo;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.transaction.ProductService;
import com.fajar.shoppingmart.service.transaction.SellingAndPurchasingService;
import com.fajar.shoppingmart.service.transaction.TransactionHistoryService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/transaction")
@Slf4j 
@Authenticated
public class RestTransactionController extends BaseController{
	
	@Autowired
	private SellingAndPurchasingService  transactionService;
	@Autowired
	private TransactionHistoryService transactionHistoryService;
	@Autowired
	private ProductService productService;

	public RestTransactionController() {
		log.info("------------------Rest Transaction Controller-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	 
	@PostMapping(value = "/purchasing", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse purchaseProduct(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("supply {}", request);
		 
		WebResponse response = transactionService.purchaseProduct(request, httpRequest);
		return response;
	} 
	
	@PostMapping(value = "/selling", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse sell(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("purchase {}", request);
		 
		WebResponse response = transactionService.sellProduct(request, httpRequest);
		return response;
	}
	
	@PostMapping(value = "/stocks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse stockinfo(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("stocks {}", request);
		if(!sessionValidationService.hasSession(httpRequest)) {
			return WebResponse.failedResponse();
		}
		WebResponse response = transactionService.getStocksByProductName(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/cashflowinfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse cashflowinfo(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("cashflowinfo {}", request);
		 
		WebResponse response = transactionHistoryService.getCashFlow(request);
		return response;
	}
	
	@PostMapping(value = "/cashflowdetail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse detailcashflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("cashflowdetail {}", request);
		 
		WebResponse response = transactionHistoryService.getCashflowDetail(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/monthlycashflow", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse monthlyDetailCasflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("monthlycashflow {}", request);
		 
		WebResponse response = transactionHistoryService.getCashflowMonthly(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/dailycashflow", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse dailycashflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("monthlycashflow {}", request);
		 
		WebResponse response = transactionHistoryService.getCashflowDaily(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/productsales", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse productsales(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("productsales {}", request);
		 
		WebResponse response = productService.getProductSales(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/productsalesdetail/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse productsalesdetail(@PathVariable(required = true, name="id") Long productId,@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("productsales {}", request);
		 
		WebResponse response = productService.getProductSalesDetail(request,productId, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/transactiondata/{code}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse transactiondata(@PathVariable(required = true, name="code") String code, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException { 
		 
		WebResponse response = transactionHistoryService.getTransactionData(code);
		return response;
	}
	
	

}
