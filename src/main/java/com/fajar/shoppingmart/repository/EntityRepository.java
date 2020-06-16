package com.fajar.shoppingmart.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Capital;
import com.fajar.shoppingmart.entity.CapitalFlow;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Cost;
import com.fajar.shoppingmart.entity.CostFlow;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.CustomerVoucher;
import com.fajar.shoppingmart.entity.Menu;
import com.fajar.shoppingmart.entity.Message;
import com.fajar.shoppingmart.entity.Page;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.RegisteredRequest;
import com.fajar.shoppingmart.entity.ShopProfile;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.Unit;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.UserRole;
import com.fajar.shoppingmart.entity.Voucher;
import com.fajar.shoppingmart.entity.setting.EntityManagementConfig;
import com.fajar.shoppingmart.service.entity.BaseEntityUpdateService;
import com.fajar.shoppingmart.service.entity.CapitalFlowUpdateService;
import com.fajar.shoppingmart.service.entity.CommonUpdateService;
import com.fajar.shoppingmart.service.entity.CostFlowUpdateService;
import com.fajar.shoppingmart.service.entity.EntityUpdateInterceptor;
import com.fajar.shoppingmart.service.entity.ProductUpdateService;
import com.fajar.shoppingmart.service.entity.ShopProfileUpdateService;
import com.fajar.shoppingmart.service.entity.UserUpdateService;
import com.fajar.shoppingmart.service.entity.VoucherUpdateService;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class EntityRepository {

	/**
	 * Jpa Repositories
	 */
	@Autowired
	private RegisteredRequestRepository registeredRequestRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ShopProfileRepository shopProfileRepository;
	@Autowired
	private SupplierRepository supplierRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private UnitRepository unitRepository;
	@Autowired
	private CostRepository costRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private CostFlowRepository costFlowRepository;
	@Autowired
	private VoucherRepository voucherRepository;
	@Autowired
	private CustomerVoucherRepository customerVoucherRepository;
	@Autowired
	private CapitalRepository capitalRepository;
	@Autowired
	private CapitalFlowRepository capitalFlowRepository;
	@Autowired
	private PageRepository pageRepository;

	/**
	 * end jpa repos
	 */

	@Autowired
	private CommonUpdateService commonUpdateService;
	@Autowired
	private ProductUpdateService productUpdateService;
	@Autowired
	private UserUpdateService userUpdateService;
	@Autowired
	private ShopProfileUpdateService shopProfileUpdateService;
	@Autowired
	private BaseEntityUpdateService baseEntityUpdateService;
	@Autowired
	private VoucherUpdateService voucherUpdateService;
	@Autowired
	private CapitalFlowUpdateService capitalUpdateService;
	@Autowired
	private CostFlowUpdateService costFlowUpdateService;

	@PersistenceContext
	private EntityManager entityManager;

	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private final Map<String, EntityManagementConfig> entityConfiguration = new HashMap<String, EntityManagementConfig>();

	@PostConstruct
	public void init() {
		entityConfiguration.clear();

		/**
		 * commons
		 */
		toCommonUpdateService(Unit.class, Customer.class, RegisteredRequest.class, CustomerVoucher.class,
				UserRole.class, Capital.class, Cost.class, Page.class, Supplier.class, Category.class);

		/**
		 * special
		 */
		putConfig(Menu.class, commonUpdateService, EntityUpdateInterceptor.menuInterceptor());
		putConfig(Product.class, productUpdateService);
		putConfig(User.class, userUpdateService);
		putConfig(ShopProfile.class, shopProfileUpdateService);
		putConfig(CostFlow.class, costFlowUpdateService);
		putConfig(Voucher.class, voucherUpdateService);
		putConfig(CapitalFlow.class, capitalUpdateService);
		/**
		 * unable to update
		 */
		putConfig(CashBalance.class, baseEntityUpdateService);
		putConfig(Transaction.class, baseEntityUpdateService);
		putConfig(ProductFlow.class, baseEntityUpdateService);
		putConfig(Message.class, commonUpdateService);
	}

	/**
	 * put configuration to entityConfiguration without entityUpdateInterceptor
	 * 
	 * @param class1
	 * @param commonUpdateService2
	 */
	private void putConfig(Class<? extends BaseEntity> class1, BaseEntityUpdateService commonUpdateService2) {
		putConfig(class1, commonUpdateService2, null);

	}

	/**
	 * put configuration to entityConfiguration map
	 * 
	 * @param _class
	 * @param updateService
	 * @param updateInterceptor
	 */
	private void putConfig(Class<? extends BaseEntity> _class, BaseEntityUpdateService updateService,
			EntityUpdateInterceptor updateInterceptor) {
		String key = _class.getSimpleName().toLowerCase();
		entityConfiguration.put(key, config(key, _class, updateService, updateInterceptor));
	}

	/**
	 * set update service to commonUpdateService and NO update interceptor
	 * 
	 * @param classes
	 */
	private void toCommonUpdateService(Class<? extends BaseEntity>... classes) {
		for (int i = 0; i < classes.length; i++) {
			putConfig(classes[i], commonUpdateService);
		}
	}

	public EntityManagementConfig getConfig(String key) {
		return entityConfiguration.get(key);
	}

	/**
	 * construct EntityManagementConfig object
	 * 
	 * @param object
	 * @param class1
	 * @param commonUpdateService2
	 * @param updateInterceptor
	 * @return
	 */
	private EntityManagementConfig config(String object, Class<? extends BaseEntity> class1,
			BaseEntityUpdateService commonUpdateService2, EntityUpdateInterceptor updateInterceptor) {
		return new EntityManagementConfig(object, class1, commonUpdateService2, updateInterceptor);
	}

	/**
	 * save entity
	 * 
	 * @param <T>
	 * @param baseEntity
	 * @return
	 */
	public <T> T save(BaseEntity baseEntity) {
		log.info("execute method save");

		boolean joinEntityExist = validateJoinColumn(baseEntity);

		if (!joinEntityExist) {

			throw new InvalidParameterException("JOIN COLUMN INVALID");
		}

		try {
			JpaRepository repository = findRepo(baseEntity.getClass());
			log.info("found repo: " + repository);
			return (T) repository.save(baseEntity);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public boolean validateJoinColumn(BaseEntity baseEntity) {

		List<Field> joinColumns = getJoinColumn(baseEntity.getClass());

		if (joinColumns.size() == 0) {
			return true;
		}

		for (Field field : joinColumns) {

			try {
				field.setAccessible(true);
				Object value = field.get(baseEntity);
				if (value == null || (value instanceof BaseEntity) == false) {
					continue;
				}

				BaseEntity entity = (BaseEntity) value;

				JpaRepository repository = findRepo(entity.getClass());

				Optional result = repository.findById(entity.getId());

				if (result.isPresent() == false) {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		return true;
	}

	public List<Field> getJoinColumn(Class<? extends BaseEntity> clazz) {

		List<Field> joinColumns = new ArrayList<>();
		Field[] fields = clazz.getFields();

		for (Field field : fields) {
			if (field.getAnnotation(JoinColumn.class) != null) {
				joinColumns.add(field);
			}
		}

		return joinColumns;
	}

	/**
	 * find suitable repository (declared in this class) for given entity object
	 * 
	 * @param entityClass
	 * @return
	 */
	public JpaRepository findRepo(Class<? extends BaseEntity> entityClass) {

		log.info("will find repo by class: {}", entityClass);

		Class<?> clazz = this.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {

			if (field.getAnnotation(Autowired.class) == null) {
				continue;
			}

			Class<?> fieldClass = field.getType();
			Class<?> originalEntityClass = getGenericClassIndexZero(fieldClass);

			if (originalEntityClass != null && originalEntityClass.equals(entityClass)) {
				try {
					return (JpaRepository) field.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					return null;
				}
			}
		}

		return null;
	}

	/**
	 * find all entity
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> List<T> findAll(Class<? extends BaseEntity> clazz) {
		JpaRepository repository = findRepo(clazz);
		if (repository == null) {
			return new ArrayList<T>();
		}
		return repository.findAll();
	}

	/**
	 * find by id
	 * 
	 * @param clazz
	 * @param ID
	 * @return
	 */
	public Object findById(Class<? extends BaseEntity> clazz, Object ID) {
		JpaRepository repository = findRepo(clazz);

		Optional result = repository.findById(ID);
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}

	public static <T> T getGenericClassIndexZero(Class clazz) {
		Type[] interfaces = clazz.getGenericInterfaces();

		if (interfaces == null) {
			log.info("interfaces is null");
			return null;
		}

		log.info("interfaces size: {}", interfaces.length);

		for (Type type : interfaces) {

			boolean isJpaRepository = type.getTypeName().startsWith(JpaRepository.class.getCanonicalName());

			if (isJpaRepository) {
				ParameterizedType parameterizedType = (ParameterizedType) type;

				if (parameterizedType.getActualTypeArguments() != null
						&& parameterizedType.getActualTypeArguments().length > 0) {
					return (T) parameterizedType.getActualTypeArguments()[0];
				}
			}
		}

		return null;
	}

	/**
	 * delete entity by id
	 * 
	 * @param id
	 * @param class1
	 * @return
	 */
	public boolean deleteById(Long id, Class<? extends BaseEntity> class1) {
		log.info("Will delete entity: {}, id: {}", class1.getClass(), id);

		try {

			JpaRepository repository = findRepo(class1);
			repository.deleteById(id);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public EntityManagementConfig getConfiguration(String key) {
		return this.entityConfiguration.get(key);
	}

}
