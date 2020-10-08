package com.fajar.shoppingmart.service.sessions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.RegisteredRequest;

public interface RegisteredRequestService {
	
public RegisteredRequest getRegisteredRequest(String requestId);
	
	public WebResponse getAvailableSessions();
	public WebResponse deleteSession(WebRequest request);
	public WebResponse clearSessions();
	public WebResponse generateRequestId(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

}
