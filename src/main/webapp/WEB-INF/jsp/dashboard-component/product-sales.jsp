<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div id="content-product-sales" style="display: none"> 
	<h3>
		Product Sales <small>
		<span  id="info-sales-period"></span>
		<a href="#" class="badge badge-info" onclick="closeProductSales()">Close</a>
		</small>
	</h3> 
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

	function closeProductSales() {
		if(!confirm("Close this menu?")) { return; }
		hide('content-product-sales');
		hide('filter-wrapper');
		show('content-dashboard')
	}

	function showProductSales() {
		document.getElementById("btn-ok-filter-detail").onclick = function() {
			tableSales.innerHTML = "";
			showSales();
		};
		this.currentOffset = 0;
		show('content-product-sales');
		hide('content-dashboard');
		show('filter-wrapper');
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

		const monthFrom = monthNames[selectMonthFrom.value - 1];
		const monthTo = monthNames[selectMonthTo.value - 1];
		const yearFrom = selectYearFrom.value;
		const yearTo = selectYearTo.value;

		document.getElementById("info-sales-period").innerHTML = monthFrom
				+ " " + yearFrom + " - " + monthTo + " " + yearTo;
	}
</script>