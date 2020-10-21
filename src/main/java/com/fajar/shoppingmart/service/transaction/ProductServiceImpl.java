package com.fajar.shoppingmart.service.transaction;

import static com.fajar.shoppingmart.util.CollectionUtil.arrayToList;
import static com.fajar.shoppingmart.util.CollectionUtil.convertList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductSales;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.repository.ProductRepository;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.entity.EntityService;
import com.fajar.shoppingmart.service.entity.EntityService.EntityResult;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService{

	private static final String withStock = "withStock";

	private static final String withNewInfo = "withNewInfo";

	private static final String withSupplier = "withSupplier";

	private static final String FIELD_PRODUCT_ID = "productId";

	private static final String withCategories = "withCategories";

	@Autowired
	private EntityService entityService;
	@Autowired
	private TransactionHistoryService transactionHistoryService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private ProductInventoryService  productInventoryService; 

	@PostConstruct
	public void init() { 
		LogProxyFactory.setLoggers(this);
	} 
	
	private boolean isTrue(Object object) {
		return (object != null && (Boolean.valueOf(object.toString())).equals(true));
	}
	
	@Override
	public WebResponse getProductsCatalog(WebRequest request, String requestId) {
		log.info("getProductsCatalog");

		Map<String, Object> fieldsFilter = request.getFilter().getFieldsFilter();
		Filter filter = SerializationUtils.clone(request.getFilter());

		boolean isWithStock = isTrue(fieldsFilter.get(withStock));
		boolean isWithSupplier = isTrue(fieldsFilter.get(withSupplier));
		boolean isWithNewInfo = isTrue(fieldsFilter.get(withNewInfo));
		boolean isWithCategories = isTrue(fieldsFilter.get(withCategories));

		log.info("withStock: {}, withSupplier: {}, withNewInfo: {}, isWithCategories: {}", isWithStock, isWithSupplier, isWithNewInfo, isWithCategories);

		request.getFilter().getFieldsFilter().remove(withStock);

		EntityService.EntityResult entityResult = entityService.filterEntities(request.getFilter(), Product.class);

		progressService.sendProgress(1, 1, 20.0, true, requestId);

		if (entityResult == null || entityResult.getEntities() == null || entityResult.getEntities().size() == 0) {
			return new WebResponse("01", "Data Not Found");
		}
		
		List<Product> products = convertList(entityResult.getEntities());
		
		for (Product product : products) {

			if (isWithNewInfo) {
				product.setNewProduct(isNewProduct(product.getId()));
			}
			if (isWithStock) {
				int remaining = productInventoryService.getProductInventory(product);
				product.setCount(remaining);
			}
			if (isWithSupplier) {
				List<Supplier> suppliers = transactionHistoryService.getProductSupplier(product.getId(), 5, 0);
				product.setSuppliers(suppliers);
			}

			progressService.sendProgress(1, products.size(), 80, false, requestId);
		} 
		 
//		productInventoryService.refreshSessions();
		progressService.sendComplete(requestId);
		
		WebResponse response = new WebResponse();
		if(isWithCategories) {
			List<Category> categories = entityService.findAll(Category.class);
			response.setGeneralList(categories);
		}
		response.setTotalData(entityResult.getCount());
		response.setFilter(filter);
		response.setEntities(products);
		return response;
	} 

	@Override
	public WebResponse getProductSales(WebRequest request, String requestId) {
		log.info("getProductSales");

		
		Filter filter = request.getFilter();
		List<ProductSales> productSalesList = getProductSalesList(filter,  request.getProduct() == null || request.getProduct().getName() == null ? ""
				: request.getProduct().getName(), requestId);

		WebResponse response = new WebResponse();
		response.setEntities(convertList(productSalesList));
		response.setFilter(request.getFilter());
		return response;
	}
	
	
	@Override
	public WebResponse getMoreProductSupplier(WebRequest request) {
		log.info("getMoreProductSupplier");

		try {
			WebResponse response = new WebResponse();
			Filter filter = request.getFilter();
			Integer productId = (Integer) filter.getFieldsFilter().get(FIELD_PRODUCT_ID);
			List<Supplier> suppliers = getProductSupplier(productId.longValue(), filter.getPage());

			response.setEntities(CollectionUtil.convertList(suppliers));
			return response;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return WebResponse.failed(e.getMessage());
		}
	} 
	
	@Override
	public WebResponse getProductSalesDetail(WebRequest request, Long productId, String requestId) {

		Optional<Product> productOpt = productRepository.findById(productId);
		Product product = null;

		if (productOpt.isPresent()) {
			product = productOpt.get();

		} else {
			return WebResponse.failedResponse();
		}
		Filter filter = request.getFilter();
		List<ProductSales> salesList = getProductSalesListInPeriod(product, filter, requestId);
		Integer maxValue = 0;
		
		/**
		 * set sales proportion for each product sales
		 */
		for (ProductSales sales : salesList) {

			maxValue = sales.getMaxValue(); //always same
			double ratio = (Double.parseDouble(String.valueOf(sales.getSales()))
					/ Double.parseDouble(maxValue.toString()));
			double percentage = ratio * 100;

			sales.setPercentage(percentage);
		}

		WebResponse response = new WebResponse();
		response.setEntity(product);
		response.setMaxValue(maxValue.longValue());
		response.setEntities(convertList(salesList));

		progressService.sendComplete(requestId);
		return response;
	}

	
 
	@Override
	public List<String> getRandomProductImages(String imagebasePath) {
		
		try {
			List<Product> productList = productRepository.getRandomProducts(); 
			if (productList == null || productList.size() == 0) {
				return CollectionUtil.emptyList();
			}
			return imageNames(productList, imagebasePath);
		} catch (Exception e) {
			e.printStackTrace();
			return CollectionUtil.emptyList();
		}
	}

	@Override
	public WebResponse getPublicEntities(WebRequest request, String requestId) {

		if (request.getEntity().equals("product")) {
			return getProductsCatalog(request, requestId);

		} else if (request.getEntity().equals("supplier")) {
			return entityService.filter(request, null);
		}
		return WebResponse.failed("invalid option");
	}
	
	@Override
	public WebResponse getProductSuppliedBySupplier(WebRequest request) {
		try {
			long supplierId = request.getSupplier().getId();
			List<Product> products = productRepository.getProductsSuppliedBySupplier(supplierId);
			return WebResponse.builder().entities(CollectionUtil.convertList(products)).build();
		}catch (Exception e) {
			return WebResponse.failed(e);
		}
	}
	
	//////////////////////////////Privates////////////////////////////////
	
	private List<String> imageNames(List<Product> productList, String imagebasePath) {
		List<String> finalImageNames = new ArrayList<>();
		for (Product product : productList) {

			String[] imageUrls = product.getImageUrl().toString().split("~");

			for (int i = 0; i < imageUrls.length; i++) {
				imageUrls[i] = imagebasePath + imageUrls[i];

			}
			finalImageNames .addAll(arrayToList(imageUrls));
		}
		return finalImageNames;
	}
	 
	private boolean isNewProduct(Long id) {

		Transaction firstTransactionIn = transactionHistoryService.getFirstTransaction(id);
		boolean firstTransactionExists = null != firstTransactionIn && null != firstTransactionIn.getTransactionDate();

		if (firstTransactionExists) {
			long diffDays = getDiffDays(firstTransactionIn.getTransactionDate());
			if (diffDays <= 14) {
				return true;
			}
		}

		return false;
	}
 
	private long getDiffDays(Date date) {
		long diff = new Date().getTime() - date.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays;
	}
	

	private List<Supplier> getProductSupplier(long longValue, Integer page) {
		return transactionHistoryService.getProductSupplier(longValue, 5, 5 * page);
	}
	
	private ProductSales getProductSalesAt(int month, int year, Long productId) {

		try {
			Object result = productRepository.findProductSales(month, year, productId);// .getSingleResult(sql);
			int count = Integer.parseInt(result.toString());
			ProductSales sales = new ProductSales();
			sales.setSales(count);
			sales.setMonth(month);
			sales.setYear(year);
			return sales;

		} catch (Exception ex) {
			ex.printStackTrace();
			return new ProductSales();
		}
	}
 
	private int getProductSalesBetween(String periodFrom, String periodTo, Long productId) {

		try {
			Object count = productRepository.findProductSalesBetween(periodFrom, periodTo, productId);// .getSingleResult(sql);
			return Integer.parseInt(count.toString());
		} catch (Exception ex) {
//			ex.printStackTrace();
			return 0;
		}
	}
	
	private Integer getTotalPeriod(int monthFromReq, int monthToReq, int yearFrom, int yearTo) {

		int totalPeriod = 0;
		for (int runningYear = yearFrom; runningYear <= yearTo; runningYear++) {

			int monthFrom = (runningYear == yearFrom) ? monthFromReq : 1;
			int monthTo = (runningYear == yearTo) ? monthToReq : 12;

			for (int runningMonth = monthFrom; runningMonth <= monthTo; runningMonth++) {
				totalPeriod++;
			}
		}
		return totalPeriod;
	}
	
	/**
	 * get product sales for specified product & period
	 * 
	 * @param product
	 * @param periodFrom pattern: {yyyy-MM-DD}
	 * @param periodTo   pattern: {yyyy-MM-DD}
	 * @return
	 */
	private ProductSales getProductSalesInPeriod(Product product, String periodFrom, String periodTo) {
		int sales = getProductSalesBetween(periodFrom, periodTo, product.getId());

		ProductSales productSales = new ProductSales();
		productSales.setProduct(product);
		productSales.setSales(sales);

		return productSales;
	}
	
	private List<ProductSales> getProductSalesList(Filter filter, String productName, String requestId ){
		String periodFrom = DateUtil.getFullFirstDate(filter.getMonth(), filter.getYear());
		String periodTo = DateUtil.getFullFirstDate(filter.getMonthTo(), filter.getYearTo()); 

		List<Product> products = productRepository.getByLimitAndOffset(filter.getLimit(),
				filter.getLimit() * filter.getPage(), productName);

		List<ProductSales> productSalesList = new ArrayList<>();

		/**
		 * populate product sales
		 */
		for (Product product : products) {

			ProductSales productSales = getProductSalesInPeriod(product, periodFrom, periodTo);
			productSalesList.add(productSales);
			progressService.sendProgress(1, products.size(), 100, false, requestId);
		}
		
		return productSalesList;
	}
	
	private List<ProductSales> getProductSalesListInPeriod(Product product, Filter filter, String requestId){
		 
		int monthFromReq = filter.getMonth();
		int yearFrom = filter.getYear();
		int monthToReq = filter.getMonthTo();
		int yearTo = filter.getYearTo();

		Integer maxValue = 0;
		Integer totalPeriod = 0;
		Integer runningPeriod = 0;

		/**
		 * calculate total months
		 */
		totalPeriod = getTotalPeriod(monthFromReq, monthToReq, yearFrom, yearTo);

		/**
		 * get sales data for each period
		 */
		List<ProductSales> productSalesList = new ArrayList<>();
		for (int runningYear = yearFrom; runningYear <= yearTo; runningYear++) {

			int beginningMonth = runningYear == yearFrom ? monthFromReq : 1;
			int endOfMonth = runningYear == yearTo ? monthToReq : 12;

			for (int runningMonth = beginningMonth; runningMonth <= endOfMonth; runningMonth++) {

				runningPeriod++;
				ProductSales productSales = getProductSalesAt(runningMonth, runningYear, product.getId());

				productSalesList.add(productSales);

				/**
				 * update maxValue
				 */
				if (productSales.getSales() > maxValue) {
					maxValue = productSales.getSales();
				}

				progressService.sendProgress(1, totalPeriod, 100, false, requestId);
			}
		}
		final int m = maxValue;
		productSalesList.forEach(p->{
			p.setMaxValue(m);
		});
		
		return productSalesList;
	}
	

}
