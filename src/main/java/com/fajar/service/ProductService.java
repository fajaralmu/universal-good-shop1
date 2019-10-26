package com.fajar.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Product;
import com.fajar.repository.ProductRepository;
import com.fajar.util.StringUtil;

@Service
public class ProductService {

	@Autowired
	private EntityService entityService;
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private ProductRepository productRepository;
	
	@PostConstruct
	public void init() {
//		List<Product> products = productRepository.findAll();
//		for (Product product : products) {
//			product.setCode(product.getType()+"-"+StringUtil.generateRandomNumber(5));
//			
//			productRepository.save(product);
//		}
	}
	
	public ShopApiResponse getProductsCatalog(ShopApiRequest request) {
		 
		boolean withStock = false;
		Map<String, Object> filter = request.getFilter().getFieldsFilter();
		if(filter.get("withStock")!=null && (Boolean)filter.get("withStock") == true) {
			withStock = true;
		}
		request.getFilter().getFieldsFilter().remove("withStock");
		ShopApiResponse filteredProducts = entityService.filter(request);
		if (filteredProducts == null || filteredProducts.getEntities() == null
				|| filteredProducts.getEntities().size() == 0) {
			return new ShopApiResponse("01", "Data Not Found");

		}
		List<BaseEntity> entities = filteredProducts.getEntities();
		List<Product> products = new ArrayList<Product>();
		for (BaseEntity entity : entities) {
			products.add((Product) entity);
		}
		if(withStock)
			products = transactionService.populateProductWithStocks(products, true);
		 
		filteredProducts.setEntities(convertList(products));
		return filteredProducts;
	}
	 

	public static <T> List<T> convertList(List list) {
		List<T> newList = new ArrayList<T>();
		for (Object object : list) {
			newList.add((T) object);
		}
		return newList;
	}
}
