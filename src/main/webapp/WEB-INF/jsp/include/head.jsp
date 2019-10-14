
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
 
<div class="header">
<h2>Universal Shop</h2>
<div>
	<a href="<spring:url value="/admin/home"/>" class="btn btn-default btn-lg" role="button"> Home </a>
	<a href="<spring:url value="/index"/>" class="btn btn-default btn-lg" role="button"> Main Page </a>
	<a href="<spring:url value="/admin/transaction"/>" class="btn btn-default btn-lg" role="button"> Home </a>
</div>
</div> 