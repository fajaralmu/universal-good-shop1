package com.fajar.service;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Cost;
import com.fajar.entity.Unit;
import com.fajar.entity.UserRole;
import com.fajar.entity.setting.EntityManagementConfig;
import com.fajar.repository.EntityRepository;
import com.fajar.repository.RepositoryCustomImpl;
import com.fajar.service.entity.BaseEntityUpdateService;
import com.fajar.util.EntityUtil;
import com.fajar.util.QueryUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityService {
 
	public static final String ORIGINAL_PREFFIX = "{ORIGINAL>>";
	public static final String PRODUCT_IMG_PREFFIX = "PRD";
	  
	@Autowired
	private RepositoryCustomImpl repositoryCustom;   
	@Autowired
	private EntityRepository entityRepository;
	 

	private Map<String, EntityManagementConfig> entityClasses = new HashMap<>();
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		setEntityConfig();
	}
	
	public void setEntityConfig() {
		entityClasses = entityRepository.getEntityConfiguration();
	}
	
	private EntityManagementConfig getEntityManagementConfig(String key) {
		return entityClasses.get(key);
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
			BaseEntityUpdateService updateService = getEntityManagementConfig(key).getEntityUpdateService();
			String fieldName = getEntityManagementConfig(key).getFieldName();
			Object entityValue = null;
			
			try {
				Field entityField = EntityUtil.getDeclaredField(ShopApiRequest.class, fieldName); 
				entityValue = entityField.get(request);
				
			}catch (Exception e) {
				e.printStackTrace();
				return ShopApiResponse.failed();
			} 
			
			if(entityValue != null)
				return updateService.saveEntity((BaseEntity)entityValue, newRecord);
			
		}catch (Exception e) { 
			e.printStackTrace();
		}
		
		 
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
			entityClass = getEntityManagementConfig(entityName).getEntityClass();
			
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
 
			Object countResult = repositoryCustom.getSingleResult(sqlCount);
 
			int count = countResult == null? 0: ((BigInteger) countResult).intValue(); 
			
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
  
	/**
	 * delete entity
	 * @param request
	 * @return
	 */
	public ShopApiResponse delete(ShopApiRequest request) { 
		
		try {
			Map<String, Object> filter 	= request.getFilter().getFieldsFilter();
			Long id 					= Long.parseLong(filter.get("id").toString()); 
			String entityName 			= request.getEntity().toLowerCase();
			
			Class<? extends BaseEntity> entityClass = getEntityManagementConfig(entityName).getEntityClass();
			
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
