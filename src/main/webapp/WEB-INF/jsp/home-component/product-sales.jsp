<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div id="content-product-sales" style="display: none">
	<h3>Product Sales</h3>
	<button id="sdsds" class="btn btn-info" onclick="show('filter-detail')">Show
		Filter</button>
	<button class="btn btn-secondary"
		onclick="hide('content-product-sales');hide('filter-detail'); show('content-dashboard')">Close</button>
	<table id="detail-sales" class="table">

	</table>
	<div style="text-align: center">
		<button class="btn btn-outline-success" onclick="loadMoreProduct()">More</button>
	</div>
</div>
<script type="text/javascript">
	var currentOffset = 0;
	//product sales
	var tableSales = document.getElementById("detail-sales");

	function showProductSales(){
		document.getElementById("btn-ok-filter-detail").onclick = function(){
			tableSales.innerHTML  = "";
			showSales();
		};
		this.currentOffset = 0;
		show('content-product-sales'); hide('content-dashboard');
		show('filter-detail');
		showSales();
	}
	
	function loadMoreProduct() {
		currentOffset++;
		showSales();
	}

	function showSales() {

		var requestObject = {
			"filter" : {
				"month" : selectMonthFrom.value,
				"year" : selectYearFrom.value,
				"monthTo" : selectMonthTo.value,
				"yearTo" : selectYearTo.value,
				"limit" : 10,
				"page" : this.currentOffset
			}
		};
		doLoadEntities("<spring:url value="/api/transaction/productsales" />",
				requestObject, function(response) {
					if (response.entities == null
							&& response.entities.length == 0) {
						alert("Data Not Found");
						return;
					}

					populateProductSales(response.entities);
				});

	}

	function populateProductSales(productSales) {
		let bodyRows = createTableBody([ "product.name", "sales", ],
				productSales, (this.currentOffset * 10));

		for (var i = 0; i < bodyRows.length; i++) {
			let row = bodyRows[i];
			tableSales.append(row);
		}
	}
</script>