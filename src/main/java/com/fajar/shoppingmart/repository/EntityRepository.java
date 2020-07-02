package com.fajar.shoppingmart.repository;

import java.lang.reflect.Field;
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
import org.springframework.data.repository.Repository;
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
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.entity.BaseEntityUpdateService;
import com.fajar.shoppingmart.service.entity.CapitalFlowUpdateService;
import com.fajar.shoppingmart.service.entity.CommonUpdateService;
import com.fajar.shoppingmart.service.entity.CostFlowUpdateService;
import com.fajar.shoppingmart.service.entity.EntityUpdateInterceptor;
import com.fajar.shoppingmart.service.entity.ProductUpdateService;
import com.fajar.shoppingmart.service.entity.ShopProfileUpdateService;
import com.fajar.shoppingmart.service.entity.UserUpdateService;
import com.fajar.shoppingmart.service.entity.VoucherUpdateService;
import com.sun.beans.TypeResolver;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class EntityRepository {

	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private RepositoryCustomImpl repositoryCustom;

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
				UserRole.class, Capital.class, Cost.class, Page.class, Supplier.class, Category.class,
				ProductFlow.class);

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
	public <T extends BaseEntity, ID> T save(T baseEntity) {
		log.info("execute method save");

		boolean joinEntityExist = validateJoinColumn(baseEntity);

		if (!joinEntityExist) {

			throw new InvalidParameterException("JOIN COLUMN INVALID");
		}
		
		try {
			return savev2(baseEntity);
//			JpaRepository<T, ID> repository = (JpaRepository<T, ID>) findRepo(baseEntity.getClass());
//			log.info("found repo: " + repository);
//			return (T) repository.save((T) baseEntity);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public <T extends BaseEntity> T savev2(T entity) {
		return repositoryCustom.saveObject(entity);

	}

	public <T extends BaseEntity, ID> boolean validateJoinColumn(T baseEntity) {

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

				T entity = (T) value;

				JpaRepository<T, ID> repository = (JpaRepository<T, ID>) findRepo(entity.getClass());

				ID id = (ID) entity.getId();
				Optional<T> result = repository.findById(id);

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
	public <T extends BaseEntity> JpaRepository findRepo(Class<T> entityClass) {

		log.info("will find repo by class: {}", entityClass);

		List<JpaRepository<?, ?>> jpaRepositories = webConfigService.getJpaRepositories();
		int index = 0;

		for (JpaRepository<?, ?> jpaObject : jpaRepositories) {
			log.info("{}-Repo : {}", index, jpaObject);
			Class<?> beanType = jpaObject.getClass();
			Type originalEntityClass = getJpaRepositoryFirstTypeArgument(beanType, entityClass);

			if (originalEntityClass != null && originalEntityClass.equals(entityClass)) {

				return (JpaRepository) jpaObject;

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
	public <T extends BaseEntity, ID> List<T> findAll(Class<T> clazz) {
		JpaRepository<T, ID> repository = findRepo(clazz);
		log.info("find repo for class: {} => {}", clazz, repository);
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
	public <T extends BaseEntity, ID> T findById(Class<T> clazz, ID id) {
		JpaRepository<T, ID> repository = findRepo(clazz);

		Optional<T> result = repository.findById(id);
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}

	public static Type getJpaRepositoryFirstTypeArgument(Class<?> clazz, Class<?> entityClass) {
		Type[] interfaces = clazz.getGenericInterfaces();

		log.debug("Check if {} is the meant repository");
		if (interfaces == null) {
			log.info("{} interfaces is null", clazz);
			return null;
		}

		log.debug("clazz {} interfaces size: {}", clazz, interfaces.length);
		// CollectionUtil.printArray(interfaces);

		for (Type type : interfaces) {

			boolean isJpaRepository = type.getTypeName().startsWith(Repository.class.getCanonicalName());

			if (isJpaRepository) {
				Type _type = TypeResolver.resolve(clazz, entityClass);
				log.debug("_type: {}", _type);
				if (_type.equals(entityClass)) {
					return _type;
				}
//				ParameterizedType parameterizedType = (ParameterizedType) type;
//
//				if (parameterizedType.getActualTypeArguments() != null
//						&& parameterizedType.getActualTypeArguments().length > 0) {
//					return (T) parameterizedType.getActualTypeArguments()[0];
//				}

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
