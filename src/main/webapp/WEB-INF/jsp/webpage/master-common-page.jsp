<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content" style="background-color: ${page.color}; height: 100%; padding: 10px">
	<h2><i class="fa fa-${page.getIconClass() }" aria-hidden="false"></i> ${page.name}</h2>
	<p>Good ${timeGreeting}, ${loggedUser.displayName}. Have a great day!</p>
	<p>${page.description }</p>
	<div class="row" style="grid-row-gap: 10px">
		<%-- <c:forEach var="menu" items="${page.menus }">
			<div class="col-sm-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top"  width="100" height="200" src="${host}/${contextPath}/${imagePath}/${menu.iconUrl }"
						alt="Card image cap">
					<div class="card-body" style="background-color:${menu.color }; color:${menu.fontColor }">
						<h5 class="card-title">
							 ${menu.name } 
						</h5>
						<a class="badge badge-primary"
							data-toggle="tooltip" data-placement="bottom"
							title="${menu.description }" href="<spring:url value= "${menu.url }" />">Detail</a>
					</div>
				</div>
			</div>
		</c:forEach> --%>
		<c:forEach var="menu" items="${page.menus }">
			<div class="col-2">
				<div class="menu-item shadow-sm p-3 mb-5 rounded" 
					style="width: 100%; height:80%; text-align: center; background-color:${menu.color }">
					<img style="margin-top: 10px" width="50" height="50" src="${host}/${contextPath}/${imagePath}/${menu.iconUrl }">
					<div>
						<h6><a style="color:${menu.fontColor }"
							data-toggle="tooltip" data-placement="bottom"
							title="${menu.description }" 
							href="<spring:url value= "${menu.url }" />">${menu.name }</a></h6>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
	<p></p>
</div>
<script type="text/javascript">
	byId("content-wrapper").style.padding= 0;
</script>