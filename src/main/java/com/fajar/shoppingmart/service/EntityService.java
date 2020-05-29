package com.fajar.shoppingmart.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Capital;
import com.fajar.shoppingmart.entity.Cost;
import com.fajar.shoppingmart.entity.Unit;
import com.fajar.shoppingmart.entity.UserRole;
import com.fajar.shoppingmart.entity.setting.EntityManagementConfig;
import com.fajar.shoppingmart.querybuilder.CRUDQueryHolder;
import com.fajar.shoppingmart.querybuilder.QueryUtil;
import com.fajar.shoppingmart.repository.EntityRepository;
import com.fajar.shoppingmart.repository.RepositoryCustomImpl;
import com.fajar.shoppingmart.service.entity.BaseEntityUpdateService;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityService {
 
	public static final String ORIGINAL_PREFFIX = "{ORIGINAL>>";
	  
	@Autowired
	private RepositoryCustomImpl repositoryCustom;   
	@Autowired
	private EntityRepository entityRepository; 
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this); 
	}
	 
	
	private EntityManagementConfig getEntityManagementConfig(String key) {
		return entityRepository.getConfiguration(key);
	}

	/**
	 * add & update entity
	 * @param request
	 * @param servletRequest
	 * @param newRecord
	 * @return
	 */
	public WebResponse saveEntity(WebRequest request, HttpServletRequest servletRequest, boolean newRecord) { 
		
		try {
			
			final String key = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(key);
			BaseEntityUpdateService updateService = entityConfig.getEntityUpdateService();
			String fieldName = entityConfig.getFieldName();
			Object entityValue = null;
			
			try {
				Field entityField = EntityUtil.getDeclaredField(WebRequest.class, fieldName); 
				entityValue = entityField.get(request);
				
				log.info("save {}: {}", entityField.getName(), entityValue);
				log.info("newRecord: {}", newRecord);
				
			}catch (Exception e) {
				e.printStackTrace();
				return WebResponse.failed();
			} 
			
			if(entityValue != null)
				return updateService.saveEntity((BaseEntity)entityValue, newRecord, entityConfig.getUpdateInterceptor());
			
		}catch (Exception e) { 
			e.printStackTrace();
		}
		
		 
		return WebResponse.builder().code("01").message("failed").build();
	}

	 
	/**
	 * get list of entities filtered
	 * @param request
	 * @return
	 */
	public WebResponse filter(WebRequest request) {
		Class<? extends BaseEntity> entityClass = null;
		
		Filter filter = request.getFilter();

		if (filter == null) {
			filter = new Filter();
		}
		if (filter.getFieldsFilter() == null) {
			filter.setFieldsFilter(new HashMap<String, Object>());
		}
		 
		try {
			
			String entityName = request.getEntity().toLowerCase();
			EntityManagementConfig config = getEntityManagementConfig(entityName);
			log.info("entityName: {}, config: {}", entityName, config);
			entityClass = config.getEntityClass();
			
			if(null == entityClass) {
				throw new Exception("Invalid entity");
			}
			 
			/**
			 * Generate query string
			 */
			EntityResult entityResult = filterEntities(filter, entityClass); 
			
			
			return WebResponse.builder().
					entities(EntityUtil.validateDefaultValue(entityResult.entities)).
					totalData(entityResult.count).
					filter(filter).entityClass(entityClass).
					build();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.failed();
		}
	}  
  
	private EntityResult filterEntities(Filter filter, Class<? extends BaseEntity> entityClass) {

		CRUDQueryHolder generatedQueryString = QueryUtil.generateSqlByFilter(filter, entityClass); 

		List<BaseEntity> entities = repositoryCustom.filterAndSort(generatedQueryString, entityClass);

		Object countResult = repositoryCustom.getSingleResult(generatedQueryString);

		int count = countResult == null? 0: ((BigInteger) countResult).intValue(); 
		
		return EntityResult.builder().entities(entities).count(count).build();
	}


	/**
	 * delete entity
	 * @param request
	 * @return
	 */
	public WebResponse delete(WebRequest request) { 
		
		try {
			Map<String, Object> filter 	= request.getFilter().getFieldsFilter();
			Long id 					= Long.parseLong(filter.get("id").toString()); 
			String entityName 			= request.getEntity().toLowerCase();
			
			Class<? extends BaseEntity> entityClass = getEntityManagementConfig(entityName).getEntityClass();
			
			if(null == entityClass) {
				throw new Exception("Invalid entity");
			}
			
			entityRepository.deleteById(id,entityClass); 
			 
			return WebResponse.builder().code("00").message("deleted successfully").build();
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
			return WebResponse.builder().code("01").message("failed: "+ex.getMessage()).build();
		}
	}
 

	public List<UserRole> getAllUserRole() {
		return entityRepository.findAll(UserRole.class);
	}
	
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	static class EntityResult implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7627112916142073122L;
		List<BaseEntity> entities;
		int count;
	}
	
	public List<BaseEntity> findAll(Class<? extends BaseEntity> _class){
		return entityRepository.findAll(_class);
	}
//	
	public List<Cost> getAllCostType() {
		return entityRepository.findAll(Cost.class);
	}
	
	public List<Unit> getAllUnit() { 
		return entityRepository.findAll(Unit.class);
	}


	public List<Capital> getAllCapitalType() { 
		return entityRepository.findAll(Capital.class);
	}

 

}