package com.fajar.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.JoinColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.fajar.entity.BaseEntity;
import com.fajar.service.entity.BaseEntityUpdateService;
import com.fajar.service.entity.CommonUpdateService;
import com.fajar.service.entity.MenuUpdateService;
import com.fajar.service.entity.ProductUpdateService;
import com.fajar.service.entity.ShopProfileUpdateService;
import com.fajar.service.entity.SupplierUpdateService;
import com.fajar.service.entity.UserUpdateService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class EntityRepository {

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

				if (parameterizedType.getActualTypeArguments() != null && parameterizedType.getActualTypeArguments().length > 0) {
					return (T) parameterizedType.getActualTypeArguments()[0];
				}
			}
		}

		return null;
	}

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

}
