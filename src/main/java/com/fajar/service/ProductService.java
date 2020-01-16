package com.fajar.service;

import static com.fajar.util.CollectionUtil.arrayToList;
import static com.fajar.util.CollectionUtil.convertList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Product;
import com.fajar.entity.ProductSales;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RepositoryCustom;
import com.fajar.util.DateUtil;

@Service
public class ProductService {

	@Autowired
	private EntityService entityService;
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private ProductRepository productRepository; 

	@Autowired
	private ProgressService progressService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public ShopApiResponse getProductsCatalog(ShopApiRequest request, String requestId) {
		progressService.init(requestId);
		boolean withStock = false;
		boolean withSupplier = false;
		boolean withNewInfo = false;
		Map<String, Object> filter = request.getFilter().getFieldsFilter();
		if (filter.get("withStock") != null && (Boolean) filter.get("withStock") == true) {
			withStock = true;
		}
		if (filter.get("withNewInfo") != null && (Boolean) filter.get("withNewInfo") == true) {
			withNewInfo = true;
		}
		if (filter.get("withSupplier") != null && (Boolean) filter.get("withSupplier") == true) {
			withSupplier = true;
		}

		request.getFilter().getFieldsFilter().remove("withStock");
		ShopApiResponse filteredProducts = entityService.filter(request);
		progressService.sendProgress(1, 1, 20.0, true, requestId);
		if (filteredProducts == null || filteredProducts.getEntities() == null
				|| filteredProducts.getEntities().size() == 0) {
			return new ShopApiResponse("01", "Data Not Found");

		}
		List<BaseEntity> entities = filteredProducts.getEntities();
		List<Product> products = new ArrayList<Product>();

		for (BaseEntity entity : entities) {
			Product product = (Product) entity;
			if (withNewInfo) {
				Transaction firstTransactionIn = transactionService.getFirstTransaction(product.getId());
				if (null != firstTransactionIn && null != firstTransactionIn.getTransactionDate()) {
					long diff = new Date().getTime() - firstTransactionIn.getTransactionDate().getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);
					if (diffDays <= 14) {
						product.setNewProduct(true);
					}
				}
			}
			progressService.sendProgress(1, entities.size(), 30, false, requestId);
			products.add(product);

		}
		if (withStock)
			products = transactionService.populateProductWithStocks(products, true, requestId);

		if (withSupplier) {
			for (Product product : products) {
				List<Supplier> suppliers = transactionService.getProductSupplier(product.getId(), 5, 0);
				product.setSuppliers(suppliers);
				progressService.sendProgress(1, products.size(), 20, false, requestId);
			}
		}
		progressService.sendComplete(requestId);
		filteredProducts.setFilter(request.getFilter());
		filteredProducts.setEntities(convertList(products));
		return filteredProducts;
	}

	public Integer getProductSalesAt(int month, int year, Long productId) {
		BigDecimal count = BigDecimal.ZERO;
		String sql = "select sum(product_flow.count) as productCount  from product   "
				+ "left join product_flow on product.id = product_flow.product_id "
				+ "left join `transaction` on transaction.id = product_flow.transaction_id  where transaction.`type` = 'OUT' and  "
				+ "month(transaction.transaction_date) = " + month + " and  year(transaction.transaction_date) = "
				+ year + " and product.id = " + productId;
		try {
			count = (BigDecimal) productRepository.getSingleResult(sql);
			return count.intValue();
		} catch (Exception ex) {
			return 0;
		}
	}

	public Integer getProductSalesBetween(String period1, String period2, Long productId) {
		String sql = "select sum(product_flow.count) as productCount from product_flow  "
				+ " left join `transaction` on transaction.id = product_flow.transaction_id "
				+ " where transaction.`type` = 'OUT' and product_flow.product_id = " + productId
				+ " and transaction.transaction_date >= '" + period1 + "' and " + " transaction.transaction_date <= '"
				+ period2 + "' ";
		try {
			BigDecimal count = (BigDecimal) productRepository.getSingleResult(sql);
			return count.intValue();
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public ShopApiResponse getProductSales(ShopApiRequest request, String requestId) {
		progressService.init(requestId);
		ShopApiResponse response = new ShopApiResponse();
		Filter filter = request.getFilter();
		String periodBefore = DateUtil.getFullFirstDate(filter.getMonth(), filter.getYear());
		String periodAfter = DateUtil.getFullFirstDate(filter.getMonthTo(), filter.getYearTo());

		String productName =  request.getProduct()== null || request.getProduct().getName() == null ? "":request.getProduct().getName();
		List<Product> products = productRepository.getByLimitAndOffset(filter.getLimit(),
				filter.getLimit() * filter.getPage(),productName);

		List<ProductSales> productSalesList = new ArrayList<>();
		for (Product product : products) {
			ProductSales productSales = new ProductSales();
			productSales.setProduct(product);

			int sales = getProductSalesBetween(periodBefore, periodAfter, product.getId());

			productSales.setSales(sales);
			productSalesList.add(productSales);
			progressService.sendProgress(1, products.size(), 100, false, requestId);
		}
		progressService.sendComplete(requestId);
		response.setEntities(convertList(productSalesList));
		response.setFilter(request.getFilter());
		return response;
	}

	public ShopApiResponse getMoreProductSupplier(ShopApiRequest request) {
		ShopApiResponse response = new ShopApiResponse();
		Filter filter = request.getFilter();
		Integer productId = (Integer) filter.getFieldsFilter().get("productId");
		List<Supplier> suppliers = transactionService.getProductSupplier(productId.longValue(), 5,
				5 * filter.getPage());
		List<BaseEntity> entities = new ArrayList<>();
		for (Supplier supplier : suppliers) {
			entities.add(supplier);
		}
		response.setEntities(entities);
		return response;
	}

	public ShopApiResponse getProductSalesDetail(ShopApiRequest request, Long productId, String requestId) {
		// TODO Auto-generated method stub
		progressService.init(requestId);
		System.out.println("x x x x x x Product ID: " + productId);
		Optional<Product> productOpt = productRepository.findById(productId);
		Product product = null;
		if (productOpt.isPresent()) {
			product = productOpt.get();
		} else {
			return ShopApiResponse.failedResponse();
		}
		Filter filter = request.getFilter();
		int month1 = filter.getMonth();
		int year1 = filter.getYear();
		int month2 = filter.getMonthTo();
		int year2 = filter.getYearTo();
		Integer maxValue = 0;
		Integer totalPeriod = 0;
		Integer runningPeriod = 0;

		// calculate total period
		for (int year = year1; year <= year2; year++) {
			int beginningMonth = year == year1 ? month1 : 1;
			int endOfMonth = year == year2 ? month2 : 12;
			for (int month = beginningMonth; month <= endOfMonth; month++) {
				totalPeriod++;
			}

		}

		List<ProductSales> salesList = new ArrayList<>();
		for (int year = year1; year <= year2; year++) {
			int beginningMonth = year == year1 ? month1 : 1;
			int endOfMonth = year == year2 ? month2 : 12;
			for (int month = beginningMonth; month <= endOfMonth; month++) {
				runningPeriod++;
				int productSales = getProductSalesAt(month, year, product.getId());
				ProductSales sales = new ProductSales();
				sales.setSales(productSales);
				// sales.setProduct(product);
				sales.setMonth(month);
				sales.setYear(year);
				salesList.add(sales);
				if (productSales > maxValue) {
					maxValue = productSales;
				}
				double percentage = runningPeriod.doubleValue() / totalPeriod.doubleValue();
				progressService.sendProgress(1, totalPeriod, 100, false, requestId);
			}
		}
		for (ProductSales sales : salesList) {
			double ratio = (Double.parseDouble(sales.getSales().toString()) / Double.parseDouble(maxValue.toString()));
			// System.out.println("x x x x RATIO: "+ratio);
			double percentage = ratio * 100;
			sales.setPercentage(percentage);
		}

		ShopApiResponse response = new ShopApiResponse();
		response.setEntity(product);
		response.setMaxValue(maxValue.longValue());
		response.setEntities(convertList(salesList));
		progressService.sendComplete(requestId);
		return response;
	}

	public List<String> getRandomProductImages(String imagebasePath) {
		// TODO Auto-generated method stub
		String sqlSelectImage = "select product.image_url from product where product.image_url is not null limit 7";
		Query query = productRepository.createNativeQuery(sqlSelectImage);
		List result = query.getResultList();
		if (result == null || result.size() == 0) {
			return new ArrayList<>();
		}
		List<String> rawImageUrls = new ArrayList<>();
		List<String> finalImageUrls = new ArrayList<>();
		for (Object string : result) {
			rawImageUrls.add((String) string);
		}

		for (String string : rawImageUrls) {
			String[] imageUrls = string.split("~");
			for (int i = 0; i < imageUrls.length; i++) {
				imageUrls[i] = imagebasePath + imageUrls[i];

			}
			finalImageUrls.addAll(arrayToList(imageUrls));
		}
		return finalImageUrls;
	}

	public ShopApiResponse getPublicEntities(ShopApiRequest request, String requestId) {
		
		
		if(request.getEntity().equals("product")) {
			return getProductsCatalog(request, requestId);
		}else if(request.getEntity().equals("supplier")) {
			return entityService.filter(request);
		}
		return null;
	}
}
