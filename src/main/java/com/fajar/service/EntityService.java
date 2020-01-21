package com.fajar.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;

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
import com.fajar.entity.Message;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.RegisteredRequest;
import com.fajar.entity.ShopProfile;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.Unit;
import com.fajar.entity.User;
import com.fajar.entity.UserRole;
import com.fajar.repository.CategoryRepository;
import com.fajar.repository.CustomerRepository;
import com.fajar.repository.MenuRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RegisteredRequestRepository;
import com.fajar.repository.RepositoryCustomImpl;
import com.fajar.repository.ShopProfileRepository;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.TransactionRepository;
import com.fajar.repository.UnitRepository;
import com.fajar.repository.UserRepository;
import com.fajar.repository.UserRoleRepository;
import com.fajar.util.CollectionUtil;
import com.fajar.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
	private ShopProfileRepository shopProfileRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private RegisteredRequestRepository registeredRequestRepository;
	@Autowired
	private FileService fileService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public ShopApiResponse addEntity(ShopApiRequest request, HttpServletRequest servletRequest, boolean newRecord) {

		switch (request.getEntity().toLowerCase()) {
		case "unit":
			return saveUnit(request.getUnit(), newRecord);

		case "product":
			return saveProduct(request.getProduct(), newRecord);

		case "customer":
			return saveCustomer(request.getCustomer(), newRecord);

		case "supplier":
			return saveSupplier(request.getSupplier(), newRecord);

		case "shopprofile":

			return saveProfile(request.getShopprofile(), newRecord);
		case "user":
			return saveUser(request.getUser(), newRecord);

		case "menu":
			return saveMenu(request.getMenu(), newRecord);

		case "category":
			return saveCategory(request.getCategory(), newRecord);

		case "userrole":
			return saveUserRole(request.getUserrole(), newRecord);

		case "registeredrequest":
			return saveRegisteredRequest(request.getRegisteredRequest(), newRecord);

		}

		return ShopApiResponse.builder().code("01").message("failed").build();
	}

	private ShopApiResponse saveRegisteredRequest(RegisteredRequest registeredRequest, boolean newRecord) {
		registeredRequest = (RegisteredRequest) copyNewElement(registeredRequest, newRecord);
		RegisteredRequest newRegisteredRequest = registeredRequestRepository.save(registeredRequest);
		return ShopApiResponse.builder().entity(newRegisteredRequest).build();
	}

	private ShopApiResponse saveUserRole(UserRole userRole, boolean newRecord) {
		userRole = (UserRole) copyNewElement(userRole, newRecord);
		UserRole newUserRole = userRoleRepository.save(userRole);
		return ShopApiResponse.builder().entity(newUserRole).build();
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
		String base64Image = menu.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage("MN", base64Image);
				menu.setIconUrl(imageName);
			} catch (IOException e) {

				menu.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Menu> dbMenu = menuRepository.findById(menu.getId());
				if (dbMenu.isPresent()) {
					menu.setIconUrl(dbMenu.get().getIconUrl());
				}
			}
		}
		Menu newMenu = menuRepository.save(menu);
		return ShopApiResponse.builder().entity(newMenu).build();
	}

	private ShopApiResponse saveSupplier(Supplier supplier, boolean newRecord) {

		supplier = (Supplier) copyNewElement(supplier, newRecord);
		String base64Image = supplier.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage("SPLY", base64Image);
				supplier.setIconUrl(imageName);
			} catch (IOException e) {

				supplier.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Supplier> dbSupplier = supplierRepository.findById(supplier.getId());
				if (dbSupplier.isPresent()) {
					supplier.setIconUrl(dbSupplier.get().getIconUrl());
				}
			}
		}
		Supplier newSupplier = supplierRepository.save(supplier);
		return ShopApiResponse.builder().entity(newSupplier).build();
	}

	private ShopApiResponse saveProfile(ShopProfile shopProfile, boolean newRecord) {

		shopProfile = (ShopProfile) copyNewElement(shopProfile, newRecord);
		String base64Image = shopProfile.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage("PROFILE", base64Image);
				shopProfile.setIconUrl(imageName);
			} catch (IOException e) {

				shopProfile.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Supplier> dbSupplier = supplierRepository.findById(shopProfile.getId());
				if (dbSupplier.isPresent()) {
					shopProfile.setIconUrl(dbSupplier.get().getIconUrl());
				}
			}
		}
		ShopProfile newShopProfile = shopProfileRepository.save(shopProfile);
		return ShopApiResponse.builder().entity(newShopProfile).build();
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

		String imageData = product.getImageUrl();
		if (imageData != null && !imageData.equals("")) {
			String[] base64Images = imageData.split("~");
			if (base64Images != null && base64Images.length > 0) {
				String[] imageUrls = new String[base64Images.length];
				for (int i = 0; i < base64Images.length; i++) {
					String base64Image = base64Images[i];
					if (base64Image == null || base64Image.equals(""))
						continue;
					try {
						boolean updated = true;
						String imageName = null;
						if (base64Image.startsWith("{ORIGINAL>>")) {
							String[] raw = base64Image.split("}");
							if (raw.length > 1) {
								base64Image = raw[1];
							} else {
								imageName = raw[0].replace("{ORIGINAL>>", "");
								updated = false;
							}
						}
						if (updated) {
							imageName = fileService.writeImage("PRD", base64Image);
						}
						if(null != imageName)
							imageUrls[i] = (imageName);
					} catch (IOException e) {

						product.setImageUrl(null);
						e.printStackTrace();
					}
				}
				
				List validUrls = removeNullItemFromArray(imageUrls);
				String[] arrayOfString =  CollectionUtil.toArrayOfString(validUrls);
				
				String imageUrl = String.join("~", arrayOfString);
				product.setImageUrl(imageUrl);

			}

		} else {
			if (!newRecord) {
				Optional<Product> dbProduct = productRepository.findById(product.getId());
				if (dbProduct.isPresent()) {
					product.setImageUrl(dbProduct.get().getImageUrl());
				}
			}
		}
		Product newProduct = productRepository.save(product);
		return ShopApiResponse.builder().entity(newProduct).build();
	}
	
	
	
	private List removeNullItemFromArray(String[] array) {
		List<Object> result = new ArrayList<>();
		for (String string : array) {
			if(string!=null) {
				result.add(string);
			}
		}
		return result;
		
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
		Class<? extends BaseEntity> entityClass = null;

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

		case "productflow":
		case "productFlow":
			entityClass = ProductFlow.class;
			break;

		case "shopprofile":
		case "shopProfile":
			entityClass = ShopProfile.class;
			break;

		case "userrole":
		case "userRole":
			entityClass = UserRole.class;
			break;

		case "registeredrequest":
		case "registeredRequest":
			entityClass = RegisteredRequest.class;
			break;

		case "message":
			entityClass = Message.class;
			break;

		}
		Filter filter = request.getFilter();
		String[] sqlListAndCount = generateSqlByFilter(filter, entityClass);
		String sql = sqlListAndCount[0];
		String sqlCount = sqlListAndCount[1];
		List<BaseEntity> entities = getEntitiesBySql(sql, entityClass);
		Integer count = 0;
		Object countResult = repositoryCustom.getSingleResult(sqlCount);
		if (countResult != null) {
			count = ((BigInteger) countResult).intValue();
		}
		return ShopApiResponse.builder().entities(entities).totalData(count).filter(filter).build();
	}

	/**
	 * 
	 * @param filter
	 * @param entityClass
	 * @return sql & sqlCount
	 */
	private String[] generateSqlByFilter(Filter filter, Class<? extends BaseEntity> entityClass) {

//		String entityName = request.getEntity();
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
		return new String[] { sql, sqlCount };
	}

	public List<BaseEntity> getEntitiesBySql(String sql, Class<? extends BaseEntity> entityClass) {
		List<BaseEntity> entities = repositoryCustom.filterAndSort(sql, entityClass);
		return EntityUtil.validateDefaultValue(entities);
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

	public static void main(String[] sdfdf) {
		String ss = "FAJAR [EXACTS]";
		System.out.println(ss.split("\\[EXACTS\\]")[0]);

	}

	private static String createFilterSQL(Class entityClass, Map<String, Object> filter, boolean contains,
			boolean exacts) {
		String tableName = getTableName(entityClass);
		List<String> filters = new ArrayList<String>();
		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		log.info("=======FILTER: {}", filter);
		for (final String rawKey : filter.keySet()) {
			System.out.println("................." + rawKey + ":" + filter.get(rawKey));
			String key = rawKey;
			if (filter.get(rawKey) == null)
				continue;

			boolean itemExacts = exacts;
			boolean itemContains = contains;

			if (rawKey.endsWith("[EXACTS]")) {
				itemExacts = true;
				itemContains = false;
				key = rawKey.split("\\[EXACTS\\]")[0];
			}
			System.out.println("-------KEY:" + key);
			String[] multiKey = key.split(",");
			boolean isMultiKey = multiKey.length > 1;
			if (isMultiKey) {
				key = multiKey[0];
			}

			String columnName = key;
			// check if date
			boolean dayFilter = key.endsWith("-day");
			boolean monthFilter = key.endsWith("-month");
			boolean yearFilter = key.endsWith("-year");
			if (dayFilter || monthFilter || yearFilter) {
				String fieldName = key;
				String mode = "DAY";
				String sqlItem = " $MODE(`$TABLE_NAME`.`$COLUMN_NAME`) = $VALUE ";
				if (dayFilter) {
					fieldName = key.replace("-day", "");
					mode = "DAY";

				} else if (monthFilter) {
					fieldName = key.replace("-month", "");
					mode = "MONTH";
				} else if (yearFilter) {
					fieldName = key.replace("-year", "");
					mode = "YEAR";
				}
				Field field = getFieldByName(fieldName, fields);
				if (field == null) {
					System.out.println("!!!!!!!!!!! FIELD NOT FOUND: " + fieldName);
					continue;
				}
				columnName = getColumnName(field);
				sqlItem = sqlItem.replace("$TABLE_NAME", tableName).replace("$MODE", mode)
						.replace("$COLUMN_NAME", columnName).replace("$VALUE", filter.get(key).toString());
				filters.add(sqlItem);
				continue;
			}

			Field field = getFieldByName(key, fields);

			if (field == null) {
				System.out.println("!!!!!!!Field Not Found :" + key);
				continue;
			}
			if (field.getAnnotation(Column.class) != null)
				columnName = getColumnName(field);

			String sqlItem = " `" + tableName + "`.`" + columnName + "` ";
			if (field.getAnnotation(JoinColumn.class) != null || isMultiKey) {
				Class fieldClass = field.getType();
				String joinTableName = getTableName(fieldClass);
				FormField formField = field.getAnnotation(FormField.class);
				try {
					String referenceFieldName = formField.optionItemName();
					if (isMultiKey) {
						referenceFieldName = multiKey[1];
					}
					Field fieldField = EntityUtil.getDeclaredField(fieldClass, referenceFieldName);
					String fieldColumnName = getColumnName(fieldField);
					if (fieldColumnName == null || fieldColumnName.equals("")) {
						fieldColumnName = key;
					}
					sqlItem = " `" + joinTableName + "`.`" + fieldColumnName + "` ";
				} catch (SecurityException e) {
					e.printStackTrace();
					System.out.println("!!!!!" + e.getClass() + " " + e.getMessage() + " " + fieldClass);
					continue;
				}

			}
			// rollback key to original key
			/*
			 * if (isMultiKey) { key = String.join(",", multiKey); if
			 * (rawKey.endsWith("[EXACTS]")) { key+="[EXACTS]"; } }
			 */
			if (itemContains) {
				sqlItem += " LIKE '%" + filter.get(rawKey) + "%' ";
			} else if (itemExacts) {
				sqlItem += " = '" + filter.get(rawKey) + "' ";
			}
			System.out.println("SQL ITEM: " + sqlItem + " contains :" + itemContains + ", exacts:" + itemExacts);
			filters.add(sqlItem);
		}
		if(filters == null || filters.size() == 0) {
			return "";
		}
		return " WHERE " + String.join(" AND ", filters);
	}

	private static String orderSQL(Class entityClass, String orderType, String orderBy) {
		// set order by
		Field orderByField = EntityUtil.getDeclaredField(entityClass, orderBy);
		if (orderByField == null) {
			return null;
		}
		Field idField = EntityUtil.getIdField(entityClass);
		if (idField == null) {
			return null;
		}
		String columnName = idField.getName();
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
		System.out.println("entity class: " + entityClass.getCanonicalName());
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
				break;
			case "product":
				productRepository.deleteById(id);
				break;
			case "customer":
				customerRepository.deleteById(id);
				break;
			case "supplier":
				supplierRepository.deleteById(id);
				break;
			case "user":
				userRepository.deleteById(id);
				break;
			case "menu":
				menuRepository.deleteById(id);
				break;
			case "category":
				categoryRepository.deleteById(id);
				break;
			case "shopprofile":
			case "shopProfile":
				shopProfileRepository.deleteById(id);
				break;
			case "userrole":
			case "userRole":
				userRoleRepository.deleteById(id);
				break;
			case "registeredrequest":
			case "registeredRequest":
				registeredRequestRepository.deleteById(id);
			}
			return ShopApiResponse.builder().code("00").message("deleted successfully").build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("01").message("failed").build();
		}
	}

	public List<UserRole> getAllUserRole() {
		return userRoleRepository.findAll();
	}

}
