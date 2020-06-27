package com.fajar.shoppingmart.config;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.annotation.ResourcePath;
import com.fajar.shoppingmart.controller.BaseController;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.service.UserAccountService;
import com.fajar.shoppingmart.service.UserSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InterceptorProcessor {

	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private org.springframework.context.ApplicationContext appContext;
	@Autowired
	private UserAccountService userAccountService;

	public boolean interceptApiRequest(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handlerMethod) {

		log.info("intercept api handler: {}", request.getRequestURI());

		boolean authenticationRequired = getAuthenticationAnnotation(handlerMethod) != null;
		if (authenticationRequired) {
			if (!tokenIsValidToAccessAPI(request)) {
				response.setContentType("application/json");
				try {
					response.getWriter()
							.write(objectMapper.writeValueAsString(WebResponse.failed("NOT AUTHENTICATED")));
					response.setHeader("error_message", "Invalid Authentication");
				} catch (IOException e) {
					log.error("Error writing JSON Error Response: {}", e);
				}
				return false;
			}
		}
		return true;
	}

	public boolean interceptWebPageRequest(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handlerMethod) {

		log.info("intercept webpage handler: {}", request.getRequestURI());
		boolean authenticationRequired = getAuthenticationAnnotation(handlerMethod) != null;

		log.info("URI: {} requires authentication: {}", request.getRequestURI(), authenticationRequired);

		if (authenticationRequired) {
			if (!hasSessionToAccessWebPage(request)) {
				log.info("URI: {} not authenticated, will redirect to login page", request.getRequestURI());
				BaseController.sendRedirectLogin(request, response);
				return false;
			}
		}

		return true;
	}

	private Authenticated getAuthenticationAnnotation(HandlerMethod handlerMethod) {

		Authenticated authenticated = getHandlerAnnotation(handlerMethod, Authenticated.class);
		return authenticated;
	}

	private ResourcePath getResoucePathAnnotation(HandlerMethod handlerMethod) {

		ResourcePath ResourcePath = getHandlerAnnotation(handlerMethod, ResourcePath.class);
		return ResourcePath;
	}

	private <T> T getHandlerAnnotation(HandlerMethod handlerMethod, Class annotation) {
		T annotationObject = null;
		boolean found = false;
		try {
			annotationObject = (T) handlerMethod.getMethod().getAnnotation(annotation);
			found = true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			annotationObject = (T) handlerMethod.getBeanType().getAnnotation(annotation);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return annotationObject;
	}

	private boolean tokenIsValidToAccessAPI(HttpServletRequest request) {
		return userAccountService.validateToken(request);
	}

	private boolean hasSessionToAccessWebPage(HttpServletRequest request) {
		return userSessionService.hasSession(request);
	}

	//// https://stackoverflow.com/questions/45595203/how-i-get-the-handlermethod-matchs-a-httpservletrequest-in-a-filter
	public HandlerMethod getHandlerMethod(HttpServletRequest request) {
		HandlerMethod handlerMethod = null;

		try {
			RequestMappingHandlerMapping req2HandlerMapping = (RequestMappingHandlerMapping) appContext
					.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");
			// Map<RequestMappingInfo, HandlerMethod> handlerMethods =
			// req2HandlerMapping.getHandlerMethods();
			HandlerExecutionChain handlerExeChain = req2HandlerMapping.getHandler(request);
			if (Objects.nonNull(handlerExeChain)) {
				handlerMethod = (HandlerMethod) handlerExeChain.getHandler();

				log.info("[handler method] {}", handlerMethod.getClass());
				return handlerMethod;
			}
		} catch (Exception e) {
			log.warn("Lookup the handler method ERROR", e);
		} finally {
			log.debug("URI = " + request.getRequestURI() + ", handlerMethod = " + handlerMethod);
		}

		return null;
	}

	public boolean isApi(HandlerMethod handlerMethod) {
		if (null == handlerMethod) {
			return false;
		}
		boolean hasRestController = handlerMethod.getBeanType().getAnnotation(RestController.class) != null;
		boolean hasPostMapping = handlerMethod.getMethod().getAnnotation(PostMapping.class) != null;

		return hasRestController || hasPostMapping;
	}

	public void addResources(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler,
			ModelAndView modelAndView) {
		
		ResourcePath resourcePath = getResoucePathAnnotation(handler);

		BaseController.addJavaScriptResourcePaths(modelAndView, resourcePath.scriptPaths());
		BaseController.addStylePaths(modelAndView, resourcePath.stylePaths());
		
		
	}
}
