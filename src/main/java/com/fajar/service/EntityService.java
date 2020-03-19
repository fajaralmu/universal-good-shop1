package com.fajar.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Category;
import com.fajar.entity.Cost;
import com.fajar.entity.CostFlow;
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
import com.fajar.repository.EntityRepository;
import com.fajar.repository.MenuRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RepositoryCustomImpl;
import com.fajar.repository.ShopProfileRepository;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.UserRepository;
import com.fajar.util.CollectionUtil;
import com.fajar.util.EntityUtil;
import com.fajar.util.QueryUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityService {
 
	private static final String ORIGINAL_PREFFIX = "{ORIGINAL>>";
	private static final String PRODUCT_IMG_PREFFIX = "PRD";
	@Autowired
	private ProductRepository productRepository; 
	@Autowired
	private SupplierRepository supplierRepository;
	@Autowired
	private RepositoryCustomImpl repositoryCustom; 
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private ShopProfileRepository shopProfileRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FileService fileService;
	@Autowired
	private EntityRepository entityRepository;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public ShopApiResponse addEntity(ShopApiRequest request, HttpServletRequest servletRequest, boolean newRecord) {

		switch (request.getEntity().toLowerCase()) {
		case "unit":
			return saveCommonEntity(request.getUnit(), newRecord);

		case "product":
			return saveProduct(request.getProduct(), newRecord);

		case "customer":
			return saveCommonEntity(request.getCustomer(), newRecord);

		case "supplier":
			return saveSupplier(request.getSupplier(), newRecord);

		case "shopprofile":

			return saveProfile(request.getShopprofile(), newRecord);
		case "user":
			return saveUser(request.getUser(), newRecord);

		case "menu":
			return saveMenu(request.getMenu(), newRecord);

		case "category":
			return saveCommonEntity(request.getCategory(), newRecord);

		case "userrole":
			return saveCommonEntity(request.getUserrole(), newRecord);

		case "registeredrequest":
			return saveCommonEntity(request.getRegisteredRequest(), newRecord);
		
		case "cost":
			return saveCommonEntity(request.getCost(), newRecord);
			
		case "costflow":
			return saveCommonEntity(request.getCostFlow(), newRecord);

		}

		return ShopApiResponse.builder().code("01").message("failed").build();
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
						if (base64Image.startsWith(ORIGINAL_PREFFIX)) {
							String[] raw = base64Image.split("}");
							if (raw.length > 1) {
								base64Image = raw[1];
							} else {
								imageName = raw[0].replace(ORIGINAL_PREFFIX, "");
								updated = false;
							}
						}
						if (updated) {
							imageName = fileService.writeImage(PRODUCT_IMG_PREFFIX, base64Image);
						}
						if (null != imageName)
							imageUrls[i] = (imageName);
					} catch (IOException e) {

						product.setImageUrl(null);
						e.printStackTrace();
					}
				}

				List validUrls = removeNullItemFromArray(imageUrls);
				String[] arrayOfString = CollectionUtil.toArrayOfString(validUrls);

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
			if (string != null) {
				result.add(string);
			}
		}
		return result;

	}

	private ShopApiResponse saveCommonEntity(BaseEntity entity, boolean newRecord) {

		entity = (BaseEntity) copyNewElement(entity, newRecord);
		BaseEntity newEntity = entityRepository.save(entity);
		return ShopApiResponse.builder().entity(newEntity).build();
	}
 

	public ShopApiResponse filter(ShopApiRequest request) {
		Class<? extends BaseEntity> entityClass = null;
		
		Filter filter = request.getFilter();

		if (filter == null) {
			filter = new Filter();
		}
		if (filter.getFieldsFilter() == null) {
			filter.setFieldsFilter(new HashMap<>());
		}
		
		try {
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
			case "cost":
				entityClass = Cost.class;
				break;
			case "costflow":
				entityClass = CostFlow.class;
				break;

			}
//			Filter filter = request.getFilter();
//			String[] sqlListAndCount = generateSqlByFilter(filter, entityClass);
//			String sql = sqlListAndCount[0];
//			String sqlCount = sqlListAndCount[1];
//			List<BaseEntity> entities = getEntitiesBySql(sql, entityClass);
//			Integer count = 0;
//			Object countResult = repositoryCustom.getSingleResult(sqlCount);
//			if (countResult != null) {
//				count = ((BigInteger) countResult).intValue();
//			}
//			return ShopApiResponse.builder().entities(entities).totalData(count).filter(filter).build();
			/**
			 * Generate query string
			 */
			String[] sqlListAndCount = QueryUtil.generateSqlByFilter(filter, entityClass);

			String sql = sqlListAndCount[0];
			String sqlCount = sqlListAndCount[1];

			List<BaseEntity> entities = repositoryCustom.filterAndSort(sql, entityClass);

			Integer count = 0;
			Object countResult = repositoryCustom.getSingleResult(sqlCount);

			if (countResult != null) {
				count = ((BigInteger) countResult).intValue();
			}
			
			return ShopApiResponse.builder().
					entities(EntityUtil.validateDefaultValue(entities)).
					totalData(count).
					filter(filter).
					build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.failed();
		}
	}  
 

	public static void main(String[] sdfdf) {
		String ss = "FAJAR [EXACTS]";
		System.out.println(ss.split("\\[EXACTS\\]")[0]);

	}  

	public ShopApiResponse delete(ShopApiRequest request) {
		Map<String, Object> filter = request.getFilter().getFieldsFilter();
		try {
			Long id = Long.parseLong(filter.get("id").toString());
			switch (request.getEntity()) {
			case "unit":
				entityRepository.deleteById(id, Unit.class);
				break;
			case "product":
				entityRepository.deleteById(id, Product.class);
				break;
			case "customer":
				entityRepository.deleteById(id, Customer.class);
				break;
			case "supplier":
				entityRepository.deleteById(id, Supplier.class);
				break;
			case "user":
				entityRepository.deleteById(id, User.class);
				break;
			case "menu":
				entityRepository.deleteById(id, Menu.class);
				break;
			case "category":
				entityRepository.deleteById(id, Category.class);
				break;
			case "shopprofile":
			case "shopProfile":
				shopProfileRepository.deleteById(id);
				break;
			case "userrole":
			case "userRole":
				entityRepository.deleteById(id, UserRole.class);
				break;
			case "registeredrequest":
			case "registeredRequest":
				entityRepository.deleteById(id, RegisteredRequest.class);
				break;
			case "cost":
				entityRepository.deleteById(id, Cost.class);
				break;
			}
			return ShopApiResponse.builder().code("00").message("deleted successfully").build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ShopApiResponse.builder().code("01").message("failed").build();
		}
	}

	public List  getAllUserRole() {
		return entityRepository.findAll(UserRole.class);
	}
	
	public List getAllCostType() {
		return entityRepository.findAll(Cost.class);
	}
	
	public List<Unit> getAllUnit() { 
		return entityRepository.findAll(Unit.class);
	}

}
