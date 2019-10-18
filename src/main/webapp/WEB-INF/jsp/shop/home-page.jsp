<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Shop::Home</title>
<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/css/bootstrap.css?version=1"></c:url> />
<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/css/shop.css?version=1"></c:url> />
<script src="<c:url value="/res/js/ajax.js"></c:url >"></script>
<script src="<c:url value="/res/js/util.js"></c:url >"></script>
<script src="<c:url value="/res/js/bootstrap.js"></c:url >"></script>
</head>
<body>
	<div class="container">
		<jsp:include page="../include/head.jsp"></jsp:include>
		<div class="content">
			<h2>Home Page</h2>
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
				<li><a href="<spring:url value="/management/user"/>">User
						Management</a></li>
					<li><a href="<spring:url value="/management/menu"/>">Menu
						Management</a></li>
				<li><a href="<spring:url value="/admin/transaction/in"/>">Incoming Product</a></li>
				<li><a href="<spring:url value="/admin/transaction/out"/>">Transaction</a></li>
				<li><a href="<spring:url value="/account/logout"/>">logout</a></li>
			</ul>
		</div>

		<jsp:include page="../include/foot.jsp"></jsp:include>
	</div>
</body>
</html>