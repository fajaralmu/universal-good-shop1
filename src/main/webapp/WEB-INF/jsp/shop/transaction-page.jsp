<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">
	<h2>Transaction Page</h2>
	<div class="row">
		<c:forEach var="menu" items="${menus }">
			<div class="col-sm-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top" width="100" height="150"
						src="${host}/${contextPath}/${imagePath}/${menu.iconUrl }"
						alt="Card image cap">
					<div class="card-body">
						<h5 class="card-title">
							 ${menu.name } 
						</h5>
						<a role="button" class="btn btn-secondary"
							data-toggle="tooltip" data-placement="bottom"
							title="${menu.description }" href="${menu.url }">Detail</a>
						<%-- <div style="height: 100px; overflow: scroll">
							<p class="card-text">${menu.description}</p>
						</div> --%>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
	<p></p>
</div>
