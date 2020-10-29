<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/res/css/bootstrap/bootstrap.min.css" />" />
<title>${title }</title>
<style>
	.container{
		width: 85%;
		margin: auto;
	}
</style>
</head>
<body>
	<div class="container">
		<h3 style="margin-top: 20px">${transaction.type} Transaction (${transaction.mode})</h3>
		<div class="row">
			<div class="col-3">Code</div>
			<div class="col-9">${transaction.code}</div>
			<c:if test="${transaction.customer != null}">
				<div class="col-3">Customer</div>
				<div class="col-9">${transaction.customer.name}</div>
			</c:if>
			<c:if test="${transaction.supplier != null}">
				<div class="col-3">Supplier</div>
				<div class="col-9">${transaction.supplier.name}</div>
			</c:if>
			<div class="col-3">Operator</div>
			<div class="col-9">${transaction.user.displayName}</div>
			<div class="col-3">Date</div>
			<div class="col-9">${transaction.transactionDate}</div>
		</div>
		<table class="table">
			<tr>
				<th>No</th>
				<th>Product Name</th>
				<th>Qty</th>
				<th>Price @item</th>
				<th>Total Price</th>
			</tr>
			<c:forEach var="productFlow" items="${transaction.productFlows }"
				varStatus="loop">
				<tr>
					<td>${loop.index+1 }</td>
					<td>${productFlow.product.name}</td>
					<td><b>${productFlow.count}</b>&nbsp;${productFlow.product.unit.name}</td>
					<td>${ (productFlow.price)}</td>
					<td>${ (productFlow.price * productFlow.count)}</td>
				</tr>
			</c:forEach>

			<tr>
				<td colSpan="2">Total</td>
				<td colSpan="2" class="font-weight-bold">${totalQuantity}</td>
				<td class="font-weight-bold">${totalPrice}</td>
			</tr>
		</table>
	</div>
</body>
</html>