package com.fajar.entity.custom;
 
import java.io.Serializable;

import com.fajar.annotation.CustomEntity;
import com.fajar.annotation.Dto;
import com.fajar.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CustomEntity(propOrder = {"count", "amount", "module"})
public class CashFlowEntity extends BaseEntity implements Serializable{

	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 4767704206272308090L;
	private Long amount;
	private Long count;
	private String module;
	
}
