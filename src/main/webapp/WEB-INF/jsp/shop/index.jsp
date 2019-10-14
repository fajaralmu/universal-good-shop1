<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Universal Shop</title>
<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/css/bootstrap.css?version=1"></c:url> />
<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/css/shop.css?version=1"></c:url> />
<script src="<c:url value="/res/js/ajax.js"></c:url >"></script>
<script src="<c:url value="/res/js/util.js"></c:url >"></script>
</head>
<body>
	<div class="container">
		<a href="<spring:url value="/account/login" />">Login</a>
		<h2>Hello, World</h2>
	</div>
</body>
</html>