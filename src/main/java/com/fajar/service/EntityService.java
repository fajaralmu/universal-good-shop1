package com.fajar.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.annotation.FormField;
import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Category;
import com.fajar.entity.Customer;
import com.fajar.entity.Menu;
import com.fajar.entity.Product;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.Unit;
import com.fajar.entity.User;
import com.fajar.entity.UserRole;
import com.fajar.repository.CategoryRepository;
import com.fajar.repository.CustomerRepository;
import com.fajar.repository.MenuRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RepositoryCustomImpl;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.TransactionRepository;
import com.fajar.repository.UnitRepository;
import com.fajar.repository.UserRepository;
import com.fajar.repository.UserRoleRepository;
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
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private TransactionRepository transactionRepository;

	public ShopApiResponse addEntity(ShopApiRequest request, boolean newRecord) {

		switch (request.getEntity().toLowerCase()) {
		case "unit":
			return saveUnit(request.getUnit(), newRecord);
		case "product":
			return saveProduct(request.getProduct(), newRecord);
		case "customer":
			return saveCustomer(request.getCustomer(), newRecord);
		case "supplier":
			return saveSupplier(request.getSupplier(), newRecord);
		case "user":
			return saveUser(request.getUser(), newRecord);
		case "menu":
			return saveMenu(request.getMenu(), newRecord);
		case "category":
			return saveCategory(request.getCategory(), newRecord);
		}

		return ShopApiResponse.builder().code("01").message("failed").build();
	}

	private ShopApiResponse saveCategory(Category category, boolean newRecord) {
		category = (Category) copyNewElement(category, newRecord);
		Category newCategory = categoryRepository.save(category);
		return ShopApiResponse.builder().entity(newCategory).build();
	}

	private ShopApiResponse saveUser(User user, boolean newRecord) {
		user = (User) copyNewElement(user, newRecord);
		User newUser = userRepository.save(user);
		return ShopApiResponse.builder().entity(newUser).build();
	}

	private ShopApiResponse saveMenu(Menu menu, boolean newRecord) {
		menu = (Menu) copyNewElement(menu, newRecord);
		Menu newMenu = menuRepository.save(menu);
		return ShopApiResponse.builder().entity(newMenu).build();
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

		switch (request.getEntity().toLowerCase()) {
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
		case "user":
			entityClass = User.class;
			break;
		case "menu":
			entityClass = Menu.class;
			break;
		case "category":
			entityClass = Category.class;
			break;
		case "transaction":
			entityClass = Transaction.class;
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
		String orderSQL = withOrder ? orderSQL(entityClass, orderType, orderBy) : "";
		String limitOffsetSQL = withLimit ? " LIMIT " + filter.getLimit() + " OFFSET " + offset : "";
		String filterSQL = withFilteredField ? createFilterSQL(entityClass, filter.getFieldsFilter(), contains, exacts)
				: "";
		String joinSql = createLeftJoinSQL(entityClass);
		String sql = "select  `" + tableName + "`.* from `" + tableName + "` " + joinSql + " " + filterSQL + orderSQL
				+ limitOffsetSQL;
		String sqlCount = "select COUNT(*) from `" + tableName + "` " + joinSql + " " + filterSQL;
		System.out.println("==============SQL: " + sql);
		List<BaseEntity> entities = repositoryCustom.filterAndSort(sql, entityClass);
		Integer count = repositoryCustom.countFilterAndSort(sqlCount);
		return ShopApiResponse.builder().entities(entities).totalData(count).filter(filter).build();
	}

	private static Field getFieldByName(String name, List<Field> fields) {
		for (Field field : fields) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}

	private static String getColumnName(Field field) {
		String columnName = ((Column) field.getAnnotation(Column.class)).name();
		if (columnName == null || columnName.equals("")) {
			columnName = field.getName();
		}
		return columnName;
	}

	public static void main(String[] sqqq) {
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("unit", "B");
		filter.put("name", "BX");
//		String sql = createFilterSQL(Product.class, filter, true, false);
		String sql = orderSQL(Product.class, "ASC", "category");
		System.out.println("==SQL: " + sql);
	}

	private static String createLeftJoinSQL(Class entityClass) {
		String tableName = getTableName(entityClass);
		String sql = "";
		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		for (Field field : fields) {
			JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
			if (joinColumn != null) {
				Class fieldClass = field.getType();
				String foreignID = joinColumn.name();
				String joinTableName = getTableName(fieldClass);
				Field idForeignField = EntityUtil.getIdField(fieldClass);
				String sqlItem = " LEFT JOIN `$JOIN_TABLE` ON  `$JOIN_TABLE`.`$JOIN_ID` = `$ENTITY_TABLE`.`$FOREIGN_ID` ";
				sqlItem = sqlItem.replace("$FOREIGN_ID", foreignID).replace("$JOIN_TABLE", joinTableName)
						.replace("$ENTITY_TABLE", tableName).replace("$JOIN_ID", getColumnName(idForeignField));
				sql += sqlItem;

			}
		}
		return sql;
	}

	private static String createFilterSQL(Class entityClass, Map<String, Object> filter, boolean contains,
			boolean exacts) {
		String tableName = getTableName(entityClass);
		List<String> filters = new ArrayList<String>();
		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		for (String key : filter.keySet()) {
			Field field = getFieldByName(key, fields);
			String columnName = key;
			if (field == null)
				continue;
			if (field.getAnnotation(Column.class) != null) {
				columnName = ((Column) field.getAnnotation(Column.class)).name();
				if (columnName == null || columnName.equals("")) {
					columnName = key;
				}
			}
			String sqlItem = " `" + tableName + "`.`" + columnName + "` ";
			if (field.getAnnotation(JoinColumn.class) != null) {
				Class fieldClass = field.getType();
				String joinTableName = getTableName(fieldClass);
				FormField formField = field.getAnnotation(FormField.class);
				try {
					Field fieldField = fieldClass.getDeclaredField(formField.optionItemName());
					String fieldColumnName = getColumnName(fieldField);
					if (fieldColumnName == null || fieldColumnName.equals("")) {
						fieldColumnName = key;
					}
					sqlItem = " `" + joinTableName + "`.`" + fieldColumnName + "` ";
				} catch (NoSuchFieldException | SecurityException e) {
					e.printStackTrace();
					continue;
				}

			}

			if (contains) {
				sqlItem += " LIKE '%" + filter.get(key) + "%' ";
			} else if (exacts) {
				sqlItem += " = '" + filter.get(key) + "' ";
			}
			filters.add(sqlItem);
		}
		return " WHERE " + String.join(" AND ", filters);
	}

	private static String orderSQL(Class entityClass, String orderType, String orderBy) {
		// set order by
		Field orderByField = EntityUtil.getDeclaredField(entityClass, orderBy);
		if (orderByField == null) {
			return null;
		}
		String columnName = "id";
		String tableName = getTableName(entityClass);
		if (orderByField.getAnnotation(JoinColumn.class) != null) {
			Class fieldClass = orderByField.getType();
			tableName = getTableName(fieldClass);
			FormField formField = orderByField.getAnnotation(FormField.class);
			try {
				Field fieldField = fieldClass.getDeclaredField(formField.optionItemName());
				columnName = getColumnName(fieldField);

			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			columnName = getColumnName(orderByField);
		}

		String orderField = "`" + tableName + "`.`" + columnName + "`";
		return " ORDER BY " + orderField + " " + orderType;
	}

	private static String getTableName(Class entityClass) {
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
			Long id = Long.parseLong(filter.get("id").toString());
			switch (request.getEntity()) {
			case "unit":
				unitRepository.deleteById(id);
			case "product":
				productRepository.deleteById(id);
			case "customer":
				customerRepository.deleteById(id);
			case "supplier":
				supplierRepository.deleteById(id);
			case "user":
				userRepository.deleteById(id);
			case "menu":
				menuRepository.deleteById(id);
			case "category":
				categoryRepository.deleteById(id);
			}
			return ShopApiResponse.builder().build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("01").message("failed").build();
		}
	}

	public List<UserRole> getAllUserRole() {
		return userRoleRepository.findAll();
	}

}
