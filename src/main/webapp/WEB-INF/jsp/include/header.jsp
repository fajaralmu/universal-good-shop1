<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="row" style="margin:0">
	<div class="col-10">
		<h1><a class="header-a" href="<c:url value="/index" />">${shopProfile.name }</a></h1>
		<p>${shopProfile.shortDescription }</p>
	</div>
	<div class="col-2" style="text-align: right;">
		<ul class="nav  flex-column" style="text-align: right;">

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
						<a class="dropdown-item"
							href="<spring:url value="/management/user"/>">User Profile</a> <a
							class="dropdown-item"
							href="<spring:url value="/management/profile"/>">App Profile</a><a
							class="dropdown-item" href="<spring:url value="/account/logout" /> ">Logout</a>
					</div>
				</div>
			</c:if>
		</ul>
	</div>
</div>
