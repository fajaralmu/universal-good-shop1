<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content" style="width:100%">

	<div id="filter-detail" class="box-shadow" style="padding:10px; display:none; width:40%; margin-right:0px; position: fixed; background-color: white; height:auto" >
		<h3>Filter</h3>
		<p>From</p>
		Month
		<select id="select-month-from"></select>
		Year
		<select id="select-year-from"></select>
		<p>To</p>
		Month
		<select id="select-month-to"></select>
		Year
		<select id="select-year-to"></select>
		 
		<button id="btn-ok-filter-detail" class="btn btn-primary btn-sm"  >Ok</button>
		<button class="btn btn-secondary  btn-sm" onclick="hide('filter-detail')">Close</button>
	</div>
	<div id="content-product-sales" style="display:none">
		<h3>Product Sales</h3>
		<button id="sdsds" class="btn btn-info" onclick="show('filter-detail')" >Show Filter</button>
		<button class="btn btn-secondary" onclick="hide('content-product-sales');hide('filter-detail'); show('content-dashboard')">Close</button>
		<table id="detail-sales" class="table">
		
		</table>
		<div style="text-align: center">
			<button class="btn btn-outline-success" onclick="loadMoreProduct()">More</button>
		</div>
	</div>
	<div id="content-detail" style="display:none">
		<h3>Cashflow History</h3>
		<button id="sdsjdh" class="btn btn-info" onclick="show('filter-detail')" >Show Filter</button>
		<button class="btn btn-secondary" onclick="hide('content-detail');hide('filter-detail'); show('content-dashboard')">Close</button>
		
		<table id="detail-cashflow" class="table">
		
		</table>
	</div>
	<div id="content-dashboard">
		<h2>Dashboard</h2>
		<div class="input-group mb-3">
			<div class="input-group-prepend">
				<span class="input-group-text">Month</span> <select
					class="form-control" id="select-month"></select> <span
					class="input-group-text">Year</span> <select class="form-control"
					id="select-year">
					 
				</select>
			</div>
			<div class="input-group-append">
				<button class="btn btn-outline-secondary" onclick="getCashflow()">OK</button>
			</div>
		</div>
		<p></p>
		<div class="row">
	
			<div class="col-sm-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top" width="100" height="150"
						src="<spring:url value="/res/img/income.jpg" />"
						alt="Card image cap">
					<div class="card-body">
						<h5 class="card-title">Income</h5>
						<ul class="list-group">
							<li
								class="list-group-item d-flex justify-content-between align-items-center">
								Product Sales <span class="badge badge-primary badge-pill"
								id="count-OUT">0</span>
							</li>
							<li
								class="list-group-item d-flex justify-content-between align-items-center">
								Income<br> <span id="amount-OUT">0</span>
							</li>
	
						</ul>
						<a role="button" id="btn-detail-OUT" class="badge badge-success"
							link="${contextPath}/management/transaction/type=OUT"
							href="${contextPath}/management/transaction/type=OUT">Detail</a>
					</div>
				</div>
			</div>
			<div class="col-sm-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top" width="100" height="150"
						src="<spring:url value="/res/img/wallet1.png" />"
						alt="Card image cap">
					<div class="card-body">
						<h5 class="card-title">Spent</h5>
						<ul class="list-group">
							<li
								class="list-group-item d-flex justify-content-between align-items-center">
								Product Supply <span class="badge badge-primary badge-pill"
								id="count-IN">0</span>
							</li>
							<li
								class="list-group-item d-flex justify-content-between align-items-center">
								Spent<br> <span id="amount-IN">0</span>
							</li>
	
						</ul>
						<a role="button" id="btn-detail-IN" class="badge badge-success"
							link="${contextPath}/management/transaction/type=IN"
							href="${contextPath}/management/transaction/type=IN">Detail</a>
					</div>
				</div>
			</div>
		</div>
		<p></p>
		<button class="btn btn-primary" onclick="showCashflowHistory()">Cashflow Detail</button>
		<button class="btn btn-primary" onclick="showProductSales()">Product Sales Detail</button>
		<p></p>
	</div>
</div>
<script type="text/javascript">
	var infoSales = document.getElementById("count-OUT");
	var infoTotalIncome = document.getElementById("amount-OUT");
	var infoSpent = document.getElementById("count-IN");
	var infoTotalSpent = document.getElementById("amount-IN");
	
	//filter
	var selectMonth = document.getElementById("select-month");
	var selectYear = document.getElementById("select-year");
	
	var selectMonthFrom = document.getElementById("select-month-from");
	var selectYearFrom = document.getElementById("select-year-from");
	
	var selectMonthTo = document.getElementById("select-month-to");
	var selectYearTo = document.getElementById("select-year-to");
	
	var selectType = document.getElementById("select-type");
	
	var btnDetailIn = document.getElementById("btn-detail-IN");
	var btnDetailOut = document.getElementById("btn-detail-OUT");
	
	//detail cashflow
	var tableDetail = document.getElementById("detail-cashflow");
	
	//product sales
	var tableSales = document.getElementById("detail-sales");

	function populatePeriodFilter() {
		populateSelectPeriod(selectMonth, selectYear);
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
		selectMonth.value = ${currentMonth};
		for(var y=${minYear};y<=${maxYear};y++){
			selectYear.append(createOption(y,y));
		}
		selectYear.value = ${currentYear};
	}

	function fetchCashflow(month, year, module) {
		infoLoading();
		var requestObject = {
				"filter":{
					"year":year,
					"month":month,
					"module":module
				}
		};
		
		console.log("get cashflow", requestObject);
		postReq("<spring:url value="/api/transaction/cashflowinfo" />"  ,
				requestObject, function(xhr) {
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						let cashflow = response.entity;
						let count =0, amount = 0;
						if(cashflow != null && cashflow.count!=null && cashflow.amount!=null ){
							count = cashflow.count;
							amount = cashflow.amount;
						}
														
						document.getElementById("count-"+module).innerHTML = count;
						document.getElementById("amount-"+module).innerHTML = beautifyNominal(amount);
						document.getElementById("btn-detail-"+module).href = document.getElementById("btn-detail-"+module).getAttribute("link")
							+"&transactionDate-month="+month+"&transactionDate-year="+year;
					} else {
						alert("Failed getting cashflow: "+module);
					} 
					infoDone();
				});
	}
	
	function getCashflow(){
		
		fetchCashflow(selectMonth.value, selectYear.value, "IN");
		fetchCashflow(selectMonth.value, selectYear.value, "OUT");
	}
	
	function showDetail(){
		
		
		var requestObject = {
				"filter":{
					"month":selectMonthFrom.value,
					"year":selectYearFrom.value,
					"monthTo":selectMonthTo.value,
					"yearTo":selectYearTo.value 
				}
		};
		tableDetail.innerHTML = "";
		postReq("<spring:url value="/api/transaction/cashflowdetail" />"  ,
				requestObject, function(xhr) {
					var supplies = xhr.data.supplies;
					var maxValue = xhr.data.maxValue; 
					var purchases = xhr.data.purchases;
					var tableColumns = [
						["No setting= <style>width:50px</style>","Period setting= <style>width:60px</style>","Amount setting=<colspan>6</colspan>"]
						
					];
					for (let i = 0; i < supplies.length; i++) {
						let cashflowSupplies = supplies[i];
						let cashflowPurchases = purchases[i];
						
						let percentSupplies = ((cashflowSupplies.amount/maxValue)*100)+"%";
						let percentPurchases = ((cashflowPurchases.amount/maxValue)*100)+"%";
						 
						let columns = [
							i+1,
							cashflowSupplies.month+"-"+cashflowSupplies.year,
							"<p style=\" font-size:0.8em;text-align:right\">"+beautifyNominal(cashflowSupplies.amount) +" ("+beautifyNominal(cashflowSupplies.count)+" unit)</p>"+
							"<p  style=\" font-size:0.8em;text-align:right\">"+beautifyNominal(cashflowPurchases.amount) +" ("+beautifyNominal(cashflowPurchases.count)+" unit)</p> setting= <style>width:200px</style>",
							//supply
							"<div class=\"rounded-right\" style=\" height: 30px;width:"+percentSupplies+"; font-size:0.7em; background-color:orange\">"+
							"</div>"+
							//purchase
							"<div class=\"rounded-right\" style=\" height: 30px;width:"+percentPurchases+"; font-size:0.7em; background-color:green\">"+
							"</div>"
							+"setting= <colspan>5</colspan>"
							];  
						 
						tableColumns.push(columns);
					}
					let tbody  = createTBodyWithGivenValue(tableColumns);
					tableDetail.innerHTML = tbody.innerHTML;
				});
		
	}
	
	var currentOffset = 0;
	
	function loadMoreProduct(){
		currentOffset++;
		showSales();
	}
	
	function showSales(){
		
		var requestObject = {
				"filter":{
					"month":selectMonthFrom.value,
					"year":selectYearFrom.value,
					"monthTo":selectMonthTo.value,
					"yearTo":selectYearTo.value,
					"limit":10,
					"page": this.currentOffset
				}
		};
		doLoadEntities("<spring:url value="/api/transaction/productsales" />"  ,
				requestObject, function(response) {
				if(response.entities == null && response.entities.length == 0){
					alert("Data Not Found");
					return;
				}
				
				populateProductSales(response.entities);
		});
		
	}
	
	function populateProductSales(productSales){
		let bodyRows = createTableBody(
				[ "product.name", "sales", ], productSales,
				(this.currentOffset * 10));

		for (var i = 0; i < bodyRows.length; i++) {
			let row = bodyRows[i];
			tableSales.append(row);
		}
	}
	
	function showCashflowHistory(){
		document.getElementById("btn-ok-filter-detail").onclick = function(){
			
			showDetail();
		};
		show('content-detail'); hide('content-dashboard');
		show('filter-detail');
		showDetail();
	}
	
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
	
	populatePeriodFilter();
	fetchCashflow(${currentMonth}, ${currentYear}, "IN");
	fetchCashflow(${currentMonth}, ${currentYear}, "OUT");
</script>
