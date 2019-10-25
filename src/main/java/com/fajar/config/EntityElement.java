package com.fajar.config;

import java.io.Serializable;
import java.util.List;

import com.fajar.annotation.Dto;
import com.fajar.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Dto
public class EntityElement implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6768302238247458766L;
	private String id; 
	private String type;
	private String className;
	private boolean identity;
	private boolean required;
	private String lableName;
	private List<BaseEntity> options;
	private String jsonList;
	private String optionItemName;
	private String optionValueName;
	private String entityReferenceName;
	private String entityReferenceClass;
	private boolean multiple;
	private boolean showDetail;
	private String detailFields;

}
