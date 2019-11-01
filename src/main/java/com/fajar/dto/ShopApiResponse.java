package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fajar.annotation.Dto;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.ProductFlowStock;
import com.fajar.entity.Transaction;
import com.fajar.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopApiResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8345271799535134609L;
	@Builder.Default
	private Date date = new Date();
	private User user;
	@Builder.Default
	private String code = "00";
	@Builder.Default
	private String message = "success";
	@Builder.Default
	private List<BaseEntity> entities = new ArrayList<>();
	private BaseEntity entity;
	private Filter filter;
	private Integer totalData;
	private Transaction transaction;
	private ProductFlowStock productFlowStock;
	private Map<String, Object> storage;

	public ShopApiResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public static ShopApiResponse failedResponse() {
		return new ShopApiResponse("01","INVALID REQUEST");
	}
}
