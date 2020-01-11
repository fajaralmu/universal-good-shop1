package com.fajar.dto;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.entity.RegisteredRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionData implements Remote, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1210492423406561769L;
	private Map<String, RegisteredRequest> registeredApps;
	
	public void addNewApp(RegisteredRequest registeredRequest) {
		if(registeredApps == null) {
			registeredApps = new HashMap<>();
		}
		registeredApps.put(registeredRequest.getRequestId(),registeredRequest);
	}
	
	public void remove(String reqId) {
		if(registeredApps == null) {
			registeredApps = new HashMap<>();
		}
		registeredApps.remove(reqId);
	}
	
	public RegisteredRequest getRequest(String reqId) {
		if(registeredApps == null) {
			registeredApps = new HashMap<>();
		}
		return registeredApps.get(reqId);
	}

}
