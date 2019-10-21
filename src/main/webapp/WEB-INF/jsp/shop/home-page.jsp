<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">
	<h2>Home Page</h2>
	<ul>
		<c:forEach var="menu" items="${menus }">
			<li><a href="${menu.url }">${menu.name }</a></li>

		</c:forEach>
		<li><a href="<spring:url value="/account/logout"/>">logout</a></li>
	</ul>
</div>
