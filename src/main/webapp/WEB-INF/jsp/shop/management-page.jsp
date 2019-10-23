<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">
	<h2>Management Page</h2>
	<ul>
		<c:forEach var="menu" items="${menus }">
			<li><a href="${menu.url }"> <img
					src="${host}/${contextPath}/${imagePath}/${menu.iconUrl }"
					width="100" height="100" />
					<h3>${menu.name }</h3>
			</a></li>

		</c:forEach>
	</ul>
</div>
