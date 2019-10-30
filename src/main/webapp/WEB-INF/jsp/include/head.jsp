
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header">

	<h2>${shopProfile.name }</h2>
	<div></div>
	<div>
		<ul class="nav nav-tabs">
			<c:if test="${loggedUser != null }">
				<li class="nav-item"><a class="nav-link">Welcome,
						${loggedUser.displayName }</a></li>
			</c:if>
			<li class="nav-item"><a
				class="nav-link ${page == 'main' ? 'active':'' }"
				href="<spring:url value="/index"/>">Main Page</a></li>
			<c:if test="${loggedUser != null }">
				<li class="nav-item"><a
					class="nav-link ${page == 'dashboard' ? 'active':'' }"
					href="<spring:url value="/admin/home"/>">Dashboard</a></li>
				<li class="nav-item"><a
					class="nav-link ${page == 'management' ? 'active':'' }"
					href="<spring:url value="/admin/management"/>">Management</a></li>
				<li class="nav-item"><a
					class="nav-link ${page == 'transaction' ? 'active':'' }"
					href="<spring:url value="/admin/transaction"/>">Transaction</a></li>
				<li class="nav-item"><a class="nav-link  "
					href="<spring:url value="/account/logout"/>">Log Out</a></li>
			</c:if>
			<li class="nav-item"><a
				class="nav-link ${page == 'about' ? 'active':'' }"
				href="<spring:url value="/public/about"/>">About Us</a></li>
			<c:if test="${loggedUser == null  }">
				<li class="nav-item "><a
					class="nav-link  ${page == 'login' ? 'active':'' }"
					href="<spring:url value="/account/login"/>">Log In </a></li>
			</c:if>

		</ul>
	</div>
</div>
<script type="text/javascript">
	var ctxPath = "${contextPath}";
</script>