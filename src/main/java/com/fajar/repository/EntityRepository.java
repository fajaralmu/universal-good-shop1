package com.fajar.repository;

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

import com.fajar.entity.BaseEntity;
import com.fajar.entity.Capital;
import com.fajar.entity.CapitalFlow;
import com.fajar.entity.CashBalance;
import com.fajar.entity.Category;
import com.fajar.entity.Cost;
import com.fajar.entity.CostFlow;
import com.fajar.entity.Customer;
import com.fajar.entity.CustomerVoucher;
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
import com.fajar.entity.Voucher;
import com.fajar.entity.setting.EntityManagementConfig;
import com.fajar.service.entity.BaseEntityUpdateService;
import com.fajar.service.entity.CapitalFlowUpdateService;
import com.fajar.service.entity.CommonUpdateService;
import com.fajar.service.entity.CostFlowUpdateService;
import com.fajar.service.entity.MenuUpdateService;
import com.fajar.service.entity.ProductUpdateService;
import com.fajar.service.entity.ShopProfileUpdateService;
import com.fajar.service.entity.SupplierUpdateService;
import com.fajar.service.entity.UserUpdateService;
import com.fajar.service.entity.VoucherUpdateService;

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

	
	/**
	 * end jpa repos
	 */
	
	
	@Autowired
	private CommonUpdateService commonUpdateService;
	@Autowired
	private MenuUpdateService menuUpdateService;
	@Autowired
	private ProductUpdateService productUpdateService;
	@Autowired
	private SupplierUpdateService supplierUpdateService;
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
		entityConfiguration.put("unit", config("unit", Unit.class, commonUpdateService));
		entityConfiguration.put("product", config("product", Product.class, productUpdateService));
		entityConfiguration.put("customer", config("customer", Customer.class, commonUpdateService));
		entityConfiguration.put("supplier", config("supplier", Supplier.class, supplierUpdateService));
		entityConfiguration.put("user", config("user", User.class, userUpdateService));
		entityConfiguration.put("menu", config("menu", Menu.class, menuUpdateService));
		entityConfiguration.put("category", config("category", Category.class, commonUpdateService));
		entityConfiguration.put("shopprofile", config("shopprofile", ShopProfile.class, shopProfileUpdateService));
		entityConfiguration.put("userrole", config("userrole", UserRole.class, commonUpdateService));
		entityConfiguration.put("registeredrequest", config("registeredRequest", RegisteredRequest.class, commonUpdateService));
		entityConfiguration.put("cost", config("cost", Cost.class, commonUpdateService));
		entityConfiguration.put("costflow", config("costflow", CostFlow.class, costFlowUpdateService));
		entityConfiguration.put("voucher", config("voucher", Voucher.class, voucherUpdateService));
		entityConfiguration.put("customervoucher", config("customervoucher", CustomerVoucher.class, commonUpdateService));
		entityConfiguration.put("capital", config("capital", Capital.class, commonUpdateService));
		entityConfiguration.put("capitalflow", config("capitalflow", CapitalFlow.class, capitalUpdateService));

		/**
		 * unable to update
		 */
		entityConfiguration.put("cashbalance", config("cashbalance", CashBalance.class, baseEntityUpdateService));
		entityConfiguration.put("transaction", config(null, Transaction.class, baseEntityUpdateService));
		entityConfiguration.put("productflow", config(null, ProductFlow.class, baseEntityUpdateService));
		entityConfiguration.put("message", config(null, Message.class, commonUpdateService));
	}
	
	public EntityManagementConfig getConfig(String key) {
		return entityConfiguration.get(key);
	}

	private EntityManagementConfig config(String object, Class<? extends BaseEntity> class1,
			BaseEntityUpdateService commonUpdateService2) {
		// TODO Auto-generated method stub
		return new EntityManagementConfig(object, class1, commonUpdateService2);
	}

	/**
	 * save entity
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
	 * @param clazz
	 * @return
	 */
	public List findAll(Class<? extends BaseEntity> clazz) {
		JpaRepository repository = findRepo(clazz);
		if (repository == null) {
			return new ArrayList<>();
		}
		return repository.findAll();
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
		return  this.entityConfiguration.get(key);
	}

}
