package com.fajar.shoppingmart.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.ProductFlowStock;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.custom.CashFlow;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data  
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebResponse implements Serializable {

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
	@Builder.Default
	private List<BaseEntity> supplies = new ArrayList<>();
	@Builder.Default
	private List<BaseEntity> purchases = new ArrayList<>();
	private BaseEntity entity;
	private Filter filter;
	private Integer totalData;
	private Transaction transaction;
	private ProductFlowStock productFlowStock;
	private Map<String, Object> storage;
	private String redirectUrl;
	private Long maxValue;
	private double percentage;
	private String requestId;
	private int[] transactionYears;
	private SessionData sessionData;
	@JsonIgnore
	private Class<? extends BaseEntity> entityClass;
	private String imageData;
	private String partnerId;
	
	private Map<Integer, CashFlow> monthlyDetailIncome;
	private Map<Integer, CashFlow> monthlyDetailCost;
	 
	private Map<String, CashFlow> dailyCashflow;
	
	public static WebResponse failedResponse() {
		return new WebResponse("01","INVALID REQUEST");
	}
	public WebResponse(String code, String message) {
		this.code = code;
		this.message = message;
		this.date = new Date();
	}
	public static WebResponse failed() {
		return   failed("INVALID REQUEST");
	}
	
	public static WebResponse failed(String msg) {
		return new WebResponse("01", msg);
	} 

	public static WebResponse success() {
		return new WebResponse("00", "SUCCESS");
	}
	public static WebResponse invalidSession() { 
		return new WebResponse("02","Invalid Session");
	}
}
