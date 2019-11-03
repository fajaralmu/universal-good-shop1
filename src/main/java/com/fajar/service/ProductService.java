package com.fajar.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
	private RepositoryCustom repositoryCustom;

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
			products.add(product);
		}
		if (withStock)
			products = transactionService.populateProductWithStocks(products, true);

		if (withSupplier) {
			for (Product product : products) {
				List<Supplier> suppliers = transactionService.getProductSupplier(product.getId(), 5, 0);
				product.setSuppliers(suppliers);
			}
		}
		filteredProducts.setFilter(request.getFilter());
		filteredProducts.setEntities(convertList(products));
		return filteredProducts;
	}

	public ShopApiResponse getProductSales(ShopApiRequest request) {
		ShopApiResponse response = new ShopApiResponse();
		Filter filter = request.getFilter();
		String periodBefore = DateUtil.getFullFirstDate(filter.getMonth(), filter.getYear());
		String periodAfter = DateUtil.getFullFirstDate(filter.getMonthTo(), filter.getYearTo());

		List<Product> products = productRepository.getByLimitAndOffset(filter.getLimit(),
				filter.getLimit() * filter.getPage());

		List<ProductSales> productSalesList = new ArrayList<>();
		for (Product product : products) {
			ProductSales productSales = new ProductSales();
			productSales.setProduct(product);
			// getting sales count
			String sql = "select sum(product_flow.count) as productCount from product_flow  "
					+ " left join `transaction` on transaction.id = product_flow.transaction_id "
					+ " where transaction.`type` = 'OUT' and product_flow.product_id = " + product.getId()
					+ " and transaction.transaction_date >= '" + periodBefore + "' and "
					+ " transaction.transaction_date <= '" + periodAfter + "' ";
			try {
				BigDecimal count = (BigDecimal) repositoryCustom.getSingleResult(sql);
				productSales.setSales(count.intValue());
			} catch (Exception ex) {
				ex.printStackTrace();
				productSales.setSales(0);
			}
			productSalesList.add(productSales);
		}
		response.setEntities(convertList(productSalesList));
		return response;
	}

	public static <T> List<T> convertList(List list) {
		List<T> newList = new ArrayList<T>();
		for (Object object : list) {
			newList.add((T) object);
		}
		return newList;
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
}
