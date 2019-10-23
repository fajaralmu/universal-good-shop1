package com.fajar.util;

import javax.servlet.http.HttpServletRequest;

public class MVCUtil {

	public static String getHost(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String uri = request.getRequestURI();
		String host = url.substring(0, url.indexOf(uri)); //result
		return host;
	}

}
