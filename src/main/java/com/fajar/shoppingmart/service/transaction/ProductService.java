package com.fajar.shoppingmart.service.transaction;

import java.util.List;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;

public interface ProductService {

	public WebResponse getProductsCatalog(WebRequest requestObject, String header);

	public WebResponse getMoreProductSupplier(WebRequest request);

	public WebResponse getPublicEntities(WebRequest request, String header);

	public WebResponse getProductSuppliedBySupplier(WebRequest request);

	public WebResponse getProductSales(WebRequest request, String header);

	public WebResponse getProductSalesDetail(WebRequest request, Long productId, String header); 

	public List<String> getRandomProductImages(String imagebasePath);

	
}
