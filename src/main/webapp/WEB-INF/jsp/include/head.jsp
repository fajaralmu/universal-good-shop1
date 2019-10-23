
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header">

	<h2>Universal Shop</h2>
	<div>
		<ul id="menu-1">

			<li><a href="<spring:url value="/index"/>"
				class="btn btn-default btn-lg" role="button"> Main Page </a></li>
			<li><a href="<spring:url value="/admin/home"/>"
				class="btn btn-default btn-lg" role="button"> Dashboard </a></li>
			<li><a href="<spring:url value="/admin/management"/>"
				class="btn btn-default btn-lg" role="button"> Management </a></li>
			<li><a href="<spring:url value="/admin/transaction"/>"
				class="btn btn-default btn-lg" role="button"> Transaction </a></li>
		</ul>

	</div>
</div>
<script type="text/javascript">
	var ctxPath = "${contextPath}";
</script>