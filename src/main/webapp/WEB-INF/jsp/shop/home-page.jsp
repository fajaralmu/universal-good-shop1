<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>HOME PAGE:: Shop</title>
</head>
<body>
	<h2>Hello, HOMIE</h2>
	<ul>
		<li><a href="<spring:url value="/"/>">index</a></li>
		<li><a href="<spring:url value="/management/unit"/>">Unit
				Management</a></li>
		<li><a href="<spring:url value="/management/customer"/>">Customer
				Management</a></li>
		<li><a href="<spring:url value="/management/supplier"/>">Supplier
				Management</a></li>
		<li><a href="<spring:url value="/management/product"/>">Product
				Management</a></li>
		<li><a href="<spring:url value="/account/logout"/>">logout</a></li>
	</ul>

</body>
</html>