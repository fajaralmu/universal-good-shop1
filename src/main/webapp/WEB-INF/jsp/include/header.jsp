<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="row header" style="margin:0;">
	<div class="col-10">
		<div class="row">
			<img class="col-1" width="50" height="50" src="${host}/${contextPath}/${imagePath}/${shopProfile.iconUrl }"  >
			<h1 class="col-11"><a class="header-a" href="<c:url value="/index" />">${shopProfile.name }</a></h1> 
			<p class="col-1"></p>
			<p class="col-11">${shopProfile.shortDescription }</p>
		</div>
	</div>
	<div class="col-2" style="text-align: right;">
		<ul class="nav  flex-column" style="text-align: right;">

			<!-- Account Menu -->
			<c:if test="${loggedUser == null  }">
				<li class="nav-item "><a
					class="nav-link  ${page == 'login' ? 'active':'' }"
					href="<spring:url value="/account/login"/>"><i class="fa fa-key" aria-hidden="true"></i> Log In </a></li>
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
