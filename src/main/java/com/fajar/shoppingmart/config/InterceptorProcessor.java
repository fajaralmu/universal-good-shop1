package com.fajar.shoppingmart.config;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
	private ApplicationContext appContext;
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
	
	public static void main(String[] args) throws  Exception {
	
//		MvcManagementController controller = new MvcManagementController();
//		Method method = controller.getClass().getMethod("commonPage", String.class, Model.class, HttpServletRequest.class, HttpServletResponse.class);
//		method.setAccessible(true);
//		HandlerMethod hm = new HandlerMethod(controller, method);
//		
//		InterceptorProcessor ip = new InterceptorProcessor();
//		Authenticated annotation = ip.getAuthenticationAnnotation(hm);
//		
//		Class<?> _class = hm.getBeanType();
//		Authenticated ano = _class.getAnnotation(Authenticated.class);
//		log.info("annotation: {}", annotation);
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
		log.debug("Get annotation: {}", annotation);
		T annotationObject = null;
		boolean found = false;
		try {
			//log.debug("handlerMethod.getMethod(): {}", handlerMethod.getMethod());
			
			annotationObject = (T) handlerMethod.getMethod().getAnnotation(annotation);
			found = annotationObject != null;
		} catch (Exception e) {
			
			log.error("Error get annotation ({}) from method", annotation);
			e.printStackTrace();
		}
		try {
			if (!found) {
				//log.debug("handlerMethod.getBeanType(): {}", handlerMethod.getBeanType());
				annotationObject = (T) handlerMethod.getBeanType().getAnnotation(annotation);
			}
		} catch (Exception e) {
			
			log.error("Error get annotation ({}) from class", annotation);
			e.printStackTrace();
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
			
			HandlerExecutionChain handlerExeChain = req2HandlerMapping.getHandler(request);
			if (Objects.nonNull(handlerExeChain)) {
				handlerMethod = (HandlerMethod) handlerExeChain.getHandler();

				log.debug("[handler method] {}", handlerMethod.getClass());
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

		log.debug("Add resourcePaths to WebPage");
		ResourcePath resourcePath = getResoucePathAnnotation(handler);

		if (null == resourcePath) {
			log.debug("{} does not have resourcePath", request.getRequestURI());
			return;
		}
		BaseController.addJavaScriptResourcePaths(modelAndView, resourcePath.scriptPaths());
		BaseController.addStylePaths(modelAndView, resourcePath.stylePaths());
		BaseController.addTitle(modelAndView, resourcePath.title());
		BaseController.addPageUrl(modelAndView, resourcePath.pageUrl());

	}
	
	public static void validateStylePaths(String[] paths) {
		if(null == paths) return;
		for (int i = 0; i < paths.length; i++) {
			if(paths[i].toString().toLowerCase().endsWith(".css") == false) {
				paths[i]+=".css?version=1";
			}
		}
	}
	public static void validateScriptPaths(String[] paths) {
		if(null == paths) return;
		for (int i = 0; i < paths.length; i++) {
			if(paths[i].toString().toLowerCase().endsWith(".js") == false) {
				paths[i]+=".js?v=1";
			}
		}
	}
}
