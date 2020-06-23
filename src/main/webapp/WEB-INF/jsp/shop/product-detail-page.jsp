<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<style>
.card-title:hover {
	cursor: pointer;
}
</style>
<div class="content">
	<div id="detail-content" class="row" style="width: 95%; margin: auto;">
	<p></p>
		<table class="table" style="layout: fixed;">
			<tr>
				<td style="width: 60%">
					
				</td>
				<td style="width: 40%">
					<h2 id="product-title">${product.name }</h2>

				</td>
			</tr>
			<tr valign="top">
				<td style="width: 60%">

					<div id="carousel-wrapper" style="width: 100%; margin: auto">
						<div id="carouselExampleIndicators" class="carousel slide"
							data-ride="carousel">
							<ol id="carousel-indicators" class="carousel-indicators">
								<%
									int i = 0;
								%>
								<c:forEach var="imageUrl" items="${imageUrlList }">
									<li class="<%=i == 0 ? "active" : ""%>" data-slide-to="<%=i%>"
										data-target="#carouselExampleIndicators"></li>
									<%
										i++;
									%>
								</c:forEach>
							</ol>
							<div id="carousel-inner" class="carousel-inner">
								<%
									i = 0;
								%>
								<c:forEach var="imageUrl" items="${imageUrlList }">
									<div class="carousel-item <%=i == 0? "active":"" %>"
										id="${imageUrl }-item">
										<img id="${imageUrl }-img" class="d-block w-100  "
											src="${host}/${contextPath}/${imagePath}/${imageUrl}"
											alt="${product.name }" width="300" height="300">
									</div>
									<%
										i++;
									%>
								</c:forEach>
							</div>
							<a class="carousel-control-prev"
								href="#carouselExampleIndicators" role="button"
								data-slide="prev"> <span class="carousel-control-prev-icon"
								aria-hidden="true"></span> <span class="sr-only">Previous</span>
							</a> <a class="carousel-control-next"
								href="#carouselExampleIndicators" role="button"
								data-slide="next"> <span class="carousel-control-next-icon"
								aria-hidden="true"></span> <span class="sr-only">Next</span>
							</a>
						</div>
					</div>
				</td>
				<!--  -->
				<td style="width: 40%">
					<!-- END CAROUSEL -->

					<ul class="list-group">
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							Stock <span class="badge badge-primary badge-pill"
							id="product-stock">${product.count }</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							Price<br> <span id="product-price">${product.price}</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							Unit<br> <span id="product-unit">${productUnit }</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							Category<br> <span id="product-category">${productCategory }</span>
						</li>
						<!-- <li
							class="list-group-item d-flex justify-content-between align-items-center">
							<button class="btn btn-primary" onclick="showproductsuppliers()">Supplier
								List</button>
						</li> -->


					</ul>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<p>Description</p>
					<p id="product-description">${product.description }</p>
				</td>
			</tr>
		</table>
	</div>
	<div  style="width: 80%; margin: auto; padding: 10px" class="border border-primary box-shadow">
		<h3>Sales Detail</h3>
		<p>From</p>
		Month <select id="select-month-from"></select> Year <select
			id="select-year-from"></select>
		<p>To</p>
		Month <select id="select-month-to"></select> Year <select
			id="select-year-to"></select>

		<button id="btn-ok-filter-detail" onclick="showSales()"
			class="btn btn-primary btn-sm">Show Sales History</button>
		<p></p>
		<table class="table" id="table-sales-history">
		</table>
	</div>
</div>
<script type="text/javascript">
	//var selectMonth = _byId("select-month");
	//var selectYear = _byId("select-year");

	var tableSales = _byId("table-sales-history");
	
	var selectMonthFrom = _byId("select-month-from");
	var selectYearFrom = _byId("select-year-from");

	var selectMonthTo = _byId("select-month-to");
	var selectYearTo = _byId("select-year-to");
	
	function populatePeriodFilter() {
	//	populateSelectPeriod(selectMonth, selectYear);
		populateSelectPeriod(selectMonthFrom, selectYearFrom);
		populateSelectPeriod(selectMonthTo, selectYearTo);
		selectMonthFrom.value = "1";
		selectMonthTo.value = "12";
	} 
	
	
	function populateSelectPeriod(selectMonth, selectYear){
		selectMonth.innerHTML = "";
		for (var i = 1; i <= 12; i++) {
			selectMonth.append(createOption(i,i));
		}
		selectMonth.value = "${currentMonth}"*1;
		for(var y="${minYear}"*1;y<="${maxYear}"*1;y++){
			selectYear.append(createOption(y,y));
		}
		selectYear.value = "${currentYear}"*1;
	}
	
	function showSales(){
		var requestObject = {
			"filter":{
					"year":selectYearFrom.value,
					"month":selectMonthFrom.value,
					"yearTo":selectYearTo.value,
					"monthTo":selectMonthTo.value
					
				}
			};
		infoLoading();
		loadEntityList("<spring:url value="/api/transaction/productsalesdetail/" />${productId}" , requestObject,function(entities){
			infoDone();
			populateTableSales(entities);
		});
	}
	
	function populateTableSales(salesList){
		tableSales.innerHTML = "";
		var tableColumns = [
			["No setting= <style>width:50px</style>","Period setting= <style>width:60px</style>","Sales setting=<colspan>6</colspan>"]
			
		];
		for (var i = 0; i < salesList.length; i++) {
			var sales = salesList[i];
			var bar = "<div class=\"rounded-right\" style=\"width: "+sales.percentage+"%;color:white; height:25px; background-color: green \"></div>setting=<colspan>5</colspan>";
			tableColumns.push(
					[
						i+1, sales.month+"-"+sales.year,beautifyNominal(sales.sales)+ "setting= <style>width:100px</style>", bar
					]
					);
		}
		let tbody  = createTBodyWithGivenValue(tableColumns);
		tableSales.innerHTML = tbody.innerHTML;
	}
	
	populatePeriodFilter();
	
</script>


		