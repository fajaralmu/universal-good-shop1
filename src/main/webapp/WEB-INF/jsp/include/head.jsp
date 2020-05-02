
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header" style="height:auto">
	<div class="page-header" style="color:${shopProfile.color}">
		<h1>${shopProfile.name }</h1>
		<p>${shopProfile.shortDescription }</p>
	</div>

	<div>
	<!-- <ul class="nav nav-tabs"> -->
		<ul class="nav  flex-column">
		
			<!-- Account Menu -->
			<c:if test="${loggedUser == null  }">
				<li class="nav-item "><a
					class="nav-link  ${page == 'login' ? 'active':'' }"
					href="<spring:url value="/account/login"/>">Log In </a></li>
			</c:if>
			<c:if test="${loggedUser != null }">
				<div class="dropdown">
					<button class="btn btn-primary dropdown-toggle" type="button"
						data-toggle="dropdown">
						${loggedUser.displayName }<span class="caret"></span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="#">Profile</a> <a
							class="dropdown-item" href="#">Setting</a> <a
							class="dropdown-item" href="#" onclick="logout()">Logout</a>
					</div>
				</div>
			</c:if>
			 
			<%--  
			<c:if test="${loggedUser != null }">
				<li class="nav-item"><a
					class="nav-link ${page == 'dashboard' ? 'active':'' }"
					href="<spring:url value="/admin/home"/>">Dashboard</a></li> 
			</c:if> --%>
		 
			<c:forEach var="pageItem" items="${pages}"> 
					<li class="nav-item"><a class="nav-link" id="${pageItem.code }"
						href="<spring:url value="${pageItem.link }"/>">${pageItem.name }</a></li>
				 
			</c:forEach>

		</ul>
	</div>
</div>
<script type="text/javascript">
	document.body.style.backgroundColor = "${shopProfile.color}";

	var ctxPath = "${contextPath}";
	function logout() {
		postReq(
				"<spring:url value="/api/account/logout" />",
				{},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {

						window.location.href = "<spring:url value="/account/login" />";
					} else {
						alert("LOGOUT FAILS");
					}
				});
	}
	
	function getCurrentPageCode(){
		postReq(
				"<spring:url value="/api/public/pagecode" />",
				{},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					var pageCode = response.code;
					_byId(pageCode).setAttribute("class", "nav-link active");
				}
		);
	}
	
	getCurrentPageCode();
</script>