package com.fajar.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Customer;
import com.fajar.entity.Product;
import com.fajar.entity.Supplier;
import com.fajar.entity.Unit;
import com.fajar.repository.CustomerRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RepositoryCustomImpl;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.UnitRepository;
import com.fajar.util.EntityUtil;

@Service
public class EntityService {

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private UnitRepository unitRepository;
	@Autowired
	private SupplierRepository supplierRepository;
	@Autowired
	private RepositoryCustomImpl repositoryCustom;

	public ShopApiResponse addEntity(ShopApiRequest request, boolean newRecord) {

		switch (request.getEntity()) {
		case "unit":
			return saveUnit(request.getUnit(), newRecord);
		case "product":
			return saveProduct(request.getProduct(), newRecord);
		case "customer":
			return saveCustomer(request.getCustomer(), newRecord);
		case "supplier":
			return saveSupplier(request.getSupplier(), newRecord);
		}

		return ShopApiResponse.builder().code("01").message("failed").build();
	}

	private ShopApiResponse saveSupplier(Supplier supplier, boolean newRecord) {

		supplier = (Supplier) copyNewElement(supplier, newRecord);
		Supplier newSupplier = supplierRepository.save(supplier);
		return ShopApiResponse.builder().entity(newSupplier).build();
	}

	private ShopApiResponse saveCustomer(Customer customer, boolean newRecord) {

		customer = (Customer) copyNewElement(customer, newRecord);
		Customer newCustomer = customerRepository.save((customer));
		return ShopApiResponse.builder().entity(newCustomer).build();
	}

	private Object copyNewElement(Object source, boolean newRecord) {
		return EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);
	}

	private ShopApiResponse saveProduct(Product product, boolean newRecord) {

		product = (Product) copyNewElement(product, newRecord);
		Product newProduct = productRepository.save(product);
		return ShopApiResponse.builder().entity(newProduct).build();
	}

	private ShopApiResponse saveUnit(Unit unit, boolean newRecord) {

		unit = (Unit) copyNewElement(unit, newRecord);
		Unit newUnit = unitRepository.save(unit);
		return ShopApiResponse.builder().entity(newUnit).build();
	}

	public List<Unit> getAllUnit() {

		return unitRepository.findAll();
	}

	public ShopApiResponse filter(ShopApiRequest request) {
		Class entityClass = null;

		switch (request.getEntity()) {
		case "unit":
			entityClass = Unit.class;
			break;
		case "product":
			entityClass = Product.class;
			break;
		case "customer":
			entityClass = Customer.class;
			break;
		case "supplier":
			entityClass = Supplier.class;
			break;
		}

		Filter filter = request.getFilter();
		String entityName = request.getEntity();
		Integer offset = filter.getPage() * filter.getLimit();
		boolean withLimit = filter.getLimit() > 0;
		boolean withOrder = filter.getOrderBy() != null && filter.getOrderType() != null
				&& !filter.getOrderBy().equals("") && !filter.getOrderType().equals("");
		boolean contains = filter.isContains();
		boolean exacts = filter.isExacts();
		boolean withFilteredField = filter.getFieldsFilter().isEmpty() == false;
		String orderType = filter.getOrderType();
		String orderBy = filter.getOrderBy();
		String tableName = getTableName(entityClass);
		String orderSQL = withOrder ? orderSQL(orderType, orderBy) : "";
		String limitSQL = withLimit ? " LIMIT " + filter.getLimit() : "";
		String filterSQL = withFilteredField ? createFilterSQL(entityClass, filter.getFieldsFilter(), contains, exacts)
				: "";

		String sql = "select * from `" + tableName + "` " + filterSQL + orderSQL + limitSQL + " OFFSET " + offset;
		String sqlCount = "select COUNT(*) from `" + tableName + "` " + filterSQL;
		System.out.println("==============SQL: " + sql);
		List<BaseEntity> entities = repositoryCustom.filterAndSort(sql, entityClass);
		Integer count = repositoryCustom.countFilterAndSort(sqlCount);
		return ShopApiResponse.builder().entities(entities).totalData(count).filter(filter).build();
	}

	private Field getFieldByName(String name, List<Field> fields) {
		for (Field field : fields) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}

	private String createFilterSQL(Class entityClass, Map<String, Object> filter, boolean contains, boolean exacts) {
		// TODO Auto-generated method stub
		List<String> filters = new ArrayList<String>();
		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		for (String key : filter.keySet()) {
			Field field = getFieldByName(key, fields);
			if (field == null)
				continue;
			String columnName = ((Column) field.getAnnotation(Column.class)).name();
			if (columnName == null || columnName.equals("")) {
				columnName = key;
			}
			String sqlItem = " `" + columnName + "` ";
			if (contains) {
				sqlItem += " LIKE '%" + filter.get(key) + "%' ";
			} else if (exacts) {
				sqlItem += " = '" + filter.get(key) + "' ";
			}
			filters.add(sqlItem);
		}
		return " WHERE " + String.join(" AND ", filters);
	}

	private String orderSQL(String orderType, String orderBy) {
		// TODO Auto-generated method stub
		return " ORDER BY `" + orderBy + "` " + orderType;
	}

	private String getTableName(Class entityClass) {
		Table table = (Table) entityClass.getAnnotation(Table.class);
		if (table != null) {
			if (table.name() != null && !table.name().equals("")) {
				return table.name();
			}
		}
		return entityClass.getSimpleName().toLowerCase();
	}

	public ShopApiResponse delete(ShopApiRequest request) {
		Map<String, Object> filter = request.getFilter().getFieldsFilter();
		try {
			switch (request.getEntity()) {
			case "unit":
				unitRepository.deleteById(Long.parseLong(filter.get("id").toString()));
			case "product":
				productRepository.deleteById(Long.parseLong(filter.get("id").toString()));
			case "customer":
				customerRepository.deleteById(Long.parseLong(filter.get("id").toString()));
			case "supplier":
				supplierRepository.deleteById(Long.parseLong(filter.get("id").toString()));
			}
			return ShopApiResponse.builder(). build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("01").message("failed").build();
		}
	}

}
