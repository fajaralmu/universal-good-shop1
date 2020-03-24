package com.fajar.service;

import java.io.IOException;
import java.lang.reflect.Field;
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
import com.fajar.entity.setting.EntityManagementConfig;
import com.fajar.repository.EntityRepository;
import com.fajar.repository.MenuRepository;
import com.fajar.repository.ProductRepository;
import com.fajar.repository.RepositoryCustomImpl;
import com.fajar.repository.ShopProfileRepository;
import com.fajar.repository.SupplierRepository;
import com.fajar.repository.UserRepository;
import com.fajar.service.entity.BaseEntityUpdateService;
import com.fajar.util.CollectionUtil;
import com.fajar.util.EntityUtil;
import com.fajar.util.QueryUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityService {
 
	public static final String ORIGINAL_PREFFIX = "{ORIGINAL>>";
	public static final String PRODUCT_IMG_PREFFIX = "PRD";
	
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
	 

	private Map<String, EntityManagementConfig> entityClasses = new HashMap<>();
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		setEntityConfig();
	}
	
	public void setEntityConfig() {
		entityClasses.put(  "unit", new EntityManagementConfig("unit", Unit.class, entityRepository.getCommonUpdateService())); 
		entityClasses.put( "product", new EntityManagementConfig("product",Product.class ,entityRepository.getProductUpdateService())); 
		entityClasses.put( "customer", new EntityManagementConfig("customer",Customer.class,entityRepository.getCommonUpdateService())); 
		entityClasses.put( "supplier", new EntityManagementConfig("supplier",Supplier.class,entityRepository.getSupplierUpdateService())); 
		entityClasses.put( "user", new EntityManagementConfig("user",User.class,entityRepository.getUserUpdateService())); 
		entityClasses.put( "menu", new EntityManagementConfig("menu",Menu.class,entityRepository.getMenuUpdateService())); 
		entityClasses.put( "category", new EntityManagementConfig("category",Category.class,entityRepository.getCommonUpdateService()));
		entityClasses.put( "shopprofile", new EntityManagementConfig("shopprofile",ShopProfile.class,entityRepository.getShopProfileUpdateService())); 
		entityClasses.put( "userrole", new EntityManagementConfig("userrole",UserRole.class,entityRepository.getCommonUpdateService())); 
		entityClasses.put( "registeredrequest", new EntityManagementConfig("registeredRequest",RegisteredRequest.class,entityRepository.getCommonUpdateService())); 
		entityClasses.put( "cost", new EntityManagementConfig("cost",Cost.class,entityRepository.getCommonUpdateService()));
		entityClasses.put( "costflow",new EntityManagementConfig("costflow",CostFlow.class,entityRepository.getCommonUpdateService())); 
	
		/**
		 * unable to update
		 */
		entityClasses.put( "transaction", new EntityManagementConfig(null,Transaction.class,entityRepository.getBaseEntityUpdateService())); 
		entityClasses.put( "productflow", new EntityManagementConfig(null,ProductFlow.class,entityRepository.getBaseEntityUpdateService())); 
		entityClasses.put( "message", new EntityManagementConfig(null,Message.class,entityRepository.getCommonUpdateService()));
		
	}

	/**
	 * add & update entity
	 * @param request
	 * @param servletRequest
	 * @param newRecord
	 * @return
	 */
	public ShopApiResponse addEntity(ShopApiRequest request, HttpServletRequest servletRequest, boolean newRecord) {

		
		try {
			
			final String key = request.getEntity().toLowerCase();
			BaseEntityUpdateService updateService = entityClasses.get(key).getEntityUpdateService();
			String fieldName = entityClasses.get(key).getFieldName();
			Object entityValue = null;
			try {
				Field entityField = EntityUtil.getDeclaredField(ShopApiRequest.class, fieldName);
//				entityField.setAccessible(true);
				entityValue = entityField.get(request);
			}catch (Exception e) {
				e.printStackTrace();
				return ShopApiResponse.failed();
			} 
			
			if(entityValue != null)
				return updateService.saveEntity((BaseEntity)entityValue, newRecord);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		/*switch (request.getEntity().toLowerCase()) {
		

		case "product":
			return saveProduct(request.getProduct(), newRecord); 
		case "supplier":
			return saveSupplier(request.getSupplier(), newRecord); 
		case "shopprofile": 
			return saveProfile(request.getShopprofile(), newRecord);
		case "user":
			return saveUser(request.getUser(), newRecord); 
		case "menu":
			return saveMenu(request.getMenu(), newRecord);
			
		/**
		 * ========================
		 *     common entities
		 * ========================
		  
		case "customer":
			return saveCommonEntity(request.getCustomer(), newRecord); 
		case "unit":
			return saveCommonEntity(request.getUnit(), newRecord);
		case "category":
			return saveCommonEntity(request.getCategory(), newRecord); 
		case "userrole":
			return saveCommonEntity(request.getUserrole(), newRecord); 
		case "registeredrequest":
			return saveCommonEntity(request.getRegisteredRequest(), newRecord); 
		case "cost":
			return saveCommonEntity(request.getCost(), newRecord); 
		case "costflow":
			return saveCommonEntity(request.getCostflow(), newRecord);

		}
		*/
		return ShopApiResponse.builder().code("01").message("failed").build();
	}

	 
	/**
	 * get list of entities filtered
	 * @param request
	 * @return
	 */
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
			
			String entityName = request.getEntity().toLowerCase();
			entityClass = entityClasses.get(entityName).getEntityClass();
			
			if(null == entityClass) {
				throw new Exception("Invalid entity");
			}
			 
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

	/**
	 * delete entity
	 * @param request
	 * @return
	 */
	public ShopApiResponse delete(ShopApiRequest request) {
		
		
		
		try {
			Map<String, Object> filter = request.getFilter().getFieldsFilter();
			Long id = Long.parseLong(filter.get("id").toString()); 
			String entityName = request.getEntity().toLowerCase();
			Class<? extends BaseEntity> entityClass = entityClasses.get(entityName).getEntityClass();
			
			if(null == entityClass) {
				throw new Exception("Invalid entity");
			}
			
			entityRepository.deleteById(id,entityClass); 
			 
			return ShopApiResponse.builder().code("00").message("deleted successfully").build();
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
			return ShopApiResponse.builder().code("01").message("failed: "+ex.getMessage()).build();
		}
	}

	public List<UserRole> getAllUserRole() {
		return entityRepository.findAll(UserRole.class);
	}
	
	public List<Cost> getAllCostType() {
		return entityRepository.findAll(Cost.class);
	}
	
	public List<Unit> getAllUnit() { 
		return entityRepository.findAll(Unit.class);
	}

}
