package com.fajar.shoppingmart.service.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.UserRole;
import com.fajar.shoppingmart.entity.setting.EntityManagementConfig;
import com.fajar.shoppingmart.repository.EntityRepository;
import com.fajar.shoppingmart.repository.RepositoryCustomImpl;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.EntityUtil;
import com.fajar.shoppingmart.util.SessionUtil;

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
	 * 
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
				
				if (entityValue != null) {
					boolean isUser = newRecord? false: checkIfUser(entityValue, servletRequest);
					WebResponse saved = updateService.saveEntity((BaseEntity) entityValue, newRecord);
					

					if (saved.isSuccess()) {
						if(isUser) {
							log.info("Updating session user");
							SessionUtil.updateSessionUser(servletRequest, (User) saved.getEntity());
						}
						validateInMemoryEntities(entityConfig);
					}

					return saved;
				} else {
					return WebResponse.failed();
				}

			} catch (Exception e) {
				e.printStackTrace();
				return WebResponse.failed(e.getMessage());
			}

		} catch (Exception e) {
			return WebResponse.failed(e.getMessage());
		} finally {

		}
	}

	private boolean checkIfUser(Object entityValue, HttpServletRequest servletRequest) {
		 
		User user = SessionUtil.getSessionUser(servletRequest);
		if(entityValue instanceof User) {
			if(!user.getId().equals(((User) entityValue).getId())){
				throw new RuntimeException("Invalid User!");
			}
			return true;
		}
		return false;
	}

	private void validateInMemoryEntities(final EntityManagementConfig entityConfig) {
//		ThreadUtil.run(()->{
//			if(entityConfig.getEntityClass().equals(Page.class)) {
//				webConfigService.refreshPages(true);
//			}else if(entityConfig.getEntityClass().equals(Menu.class)) {
//				webConfigService.refreshMenus(true);
//			}
//		});

	}

	/**
	 * get list of entities filtered
	 * 
	 * @param request
	 * @return
	 */
	public WebResponse filter(WebRequest request, HttpServletRequest httpRequest) {
		Class<? extends BaseEntity> entityClass = null;

		Filter filter = EntityUtil.cloneSerializable(request.getFilter());

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

			if (null == entityClass) {
				throw new Exception("Invalid entity");
			}
			EntityResult entityResult;
			if (User.class.equals(entityClass)) {
				User user = SessionUtil.getSessionUser(httpRequest); 
				entityResult = EntityResult.builder().count(1).entities(CollectionUtil.listOf(user)).build();
			} else {

				entityResult = filterEntities(filter, entityClass);
			}
			return WebResponse.builder()
					.entities(EntityValidation.validateDefaultValues(entityResult.entities, entityRepository))
					.totalData(entityResult.count).filter(request.getFilter()).entityClass(entityClass).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.failed(ex.getMessage());
		}
	}

	private <T extends BaseEntity> EntityResult filterEntities(Filter filter, Class<T> entityClass) {
		final List<T> entities = new ArrayList<>();
		final Map<String, Long> count = new HashMap<>();
		try {
//			Thread thread = ThreadUtil.run(() -> {
			List<T> resultList = repositoryCustom.filterAndSortv2(entityClass, filter);
			entities.addAll(resultList);
//			});
//			Thread thread2 = ThreadUtil.run(() -> {
			long resultCount = repositoryCustom.getRowCount(entityClass, filter);
			count.put("value", resultCount);
//			}); 
//			thread.join();
//			thread2.join();

		} catch (Exception e) {
			log.error("Error filterEntities: {}", e.getCause());
			count.put("value", 0L);
			e.printStackTrace();
		}
		return EntityResult.builder().entities(CollectionUtil.convertList(entities))
				.count(count.get("value").intValue()).build();
	}

	/**
	 * delete entity
	 * 
	 * @param request
	 * @return
	 */
	public WebResponse delete(WebRequest request) {

		try {
			Map<String, Object> filter = request.getFilter().getFieldsFilter();
			Long id = Long.parseLong(filter.get("id").toString());
			String entityName = request.getEntity().toLowerCase();

			Class<? extends BaseEntity> entityClass = getEntityManagementConfig(entityName).getEntityClass();

			if (null == entityClass || User.class.equals(entityClass)) {
				throw new Exception("Invalid entity");
			}

			boolean result = entityRepository.deleteById(id, entityClass);

			return WebResponse.builder().code("00").message("deleted :" + result).build();

		} catch (Exception ex) {

			ex.printStackTrace();
			return WebResponse.builder().code("01").message("failed: " + ex.getMessage()).build();
		}
	}

	public List<UserRole> getAllUserRole() {
		return entityRepository.findAll(UserRole.class);
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	static class EntityResult implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7627112916142073122L;
		List<BaseEntity> entities;
		int count;
	}
 

	public <T extends BaseEntity> List<T> findAll(Class<T> _class) {
		List<T> resultList = entityRepository.findAll(_class);

		if (null == resultList) {
			resultList = new ArrayList<T>();
		}

		return resultList;
	}

}