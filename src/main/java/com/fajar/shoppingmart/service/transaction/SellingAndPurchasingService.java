package com.fajar.shoppingmart.service.transaction;

import javax.servlet.http.HttpServletRequest;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;

public interface SellingAndPurchasingService {

	WebResponse purchaseProduct(WebRequest request, HttpServletRequest httpRequest);

	WebResponse sellProduct(WebRequest request, HttpServletRequest httpRequest);

	WebResponse getStocksByProductName(WebRequest request, String header);
 

}
