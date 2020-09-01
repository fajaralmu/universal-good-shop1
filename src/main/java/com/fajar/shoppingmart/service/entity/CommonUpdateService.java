package com.fajar.shoppingmart.service.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.annotation.StoreValueTo;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonUpdateService extends BaseEntityUpdateService<BaseEntity> {

	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord) {
		log.info("saving entity: {}", entity.getClass());
		entity = copyNewElement(entity, newRecord);

		validateEntityFields(entity, newRecord);
		interceptPreUpdate(entity);
		BaseEntity newEntity = entityRepository.save(entity);

		return WebResponse.builder().entity(newEntity).build();
	}

	/**
	 * execute things before persisting
	 * 
	 * @param entity
	 * @param updateInterceptor
	 */
	private void interceptPreUpdate(BaseEntity entity) {
		EntityUpdateInterceptor<BaseEntity> updateInterceptor = entity.getUpdateInterceptor();
		if (null != updateInterceptor && null != entity) {
			log.info("Pre Update {}", entity.getClass().getSimpleName());
			try {
				updateInterceptor.preUpdate(entity);
				log.info("success pre update");
			} catch (Exception e) {

				log.error("Error pre update entity");
				e.printStackTrace();
				throw e;
			}
		}
	}

	/**
	 * validate object properties' value
	 * 
	 * @param object
	 * @param newRecord
	 */
	private void validateEntityFields(BaseEntity object, boolean newRecord) {
		log.info("validating entity: {} newRecord: {}", object.getClass(), newRecord);
		try {

			BaseEntity existingEntity = null;
			if (!newRecord) {
				existingEntity = (BaseEntity) entityRepository.findById(object.getClass(), object.getId());
			}

			List<Field> fields = EntityUtil.getDeclaredFields(object.getClass());
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);

				try {

					FormField formfield = field.getAnnotation(FormField.class);
					if (null == formfield) {
						continue;
					}

					Object fieldValue = field.get(object);

					switch (formfield.type()) {
					case FIELD_TYPE_IMAGE:

						if (newRecord == false && fieldValue == null && existingEntity != null) {
							Object existingImage = field.get(existingEntity);
							field.set(object, existingImage);
						} else {
							String imageName = updateImage(field, object);
							field.set(object, imageName);
						}
						break;
					case FIELD_TYPE_FIXED_LIST:
						if (fieldValue == null)
							break;
						if (formfield.multipleSelect()) {
							String storeToFieldName = field.getAnnotation(StoreValueTo.class).value(); 
							
							Field idField = CollectionUtil.getIDFieldOfUnderlyingListType(field);
							Field storeToField = EntityUtil.getDeclaredField(object.getClass(), storeToFieldName);
							
							Object[] valueAsArray = ((Collection) fieldValue).toArray(); 
							CharSequence[] actualFieldValue = new String[valueAsArray.length];
							
							for (int j = 0; j < valueAsArray.length; j++) {
								actualFieldValue[j] = String.valueOf(idField.get(valueAsArray[j]));
							}
							
							storeToField.set(object, String.join("~", actualFieldValue));
						}
						break;
					default:
						break;
					}
				} catch (Exception e) {
					log.error("Error validating field: {}", field.getName());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			//
			log.error("Error validating entity {}", object.getClass().getSimpleName());
			e.printStackTrace();
		}
	}

	/**
	 * update image field, writing to disc
	 * 
	 * @param field
	 * @param object
	 * @return
	 */
	private String updateImage(Field field, BaseEntity object) {
		try {
			Object base64Value = field.get(object);
			return writeImage(object, base64Value);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			e.printStackTrace();
		}
		return null;
	}

	private String writeImage(BaseEntity object, Object base64Value) {
		String fileName = null;
		if (null != base64Value && base64Value.toString().trim().isEmpty() == false) {
			try {
				fileName = fileService.writeImage(object.getClass().getSimpleName(), base64Value.toString());
			} catch (IOException e) {
				log.error("Error writing image for {}", object.getClass().getSimpleName());
				e.printStackTrace();
			}
		}
		return fileName;
	}
}
