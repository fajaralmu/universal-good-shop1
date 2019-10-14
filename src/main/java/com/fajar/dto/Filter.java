package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.annotation.Dto;
import com.fajar.dto.ShopApiRequest.ShopApiRequestBuilder;
import com.fajar.entity.Customer;
import com.fajar.entity.Product;
import com.fajar.entity.Supplier;
import com.fajar.entity.Unit;
import com.fajar.entity.User;
import com.fajar.parameter.EntityParameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filter implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -5151185528546046666L;
	@Builder.Default
	private Integer limit = 0;
	@Builder.Default
	private Integer page = 0;
	private String orderType;
	private String orderBy;
	@Builder.Default
	private boolean contains = true;
	@Builder.Default
	private boolean beginsWith = true;
	@Builder.Default
	private boolean exacts = false;
	@Builder.Default
	private Map<String, Object> fieldsFilter = new HashMap<>();

}
