<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">
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

</div>
<script type="text/javascript">
	var infoSales = document.getElementById("count-OUT");
	var infoTotalIncome = document.getElementById("amount-OUT");
	var infoSpent = document.getElementById("count-IN");
	var infoTotalSpent = document.getElementById("amount-IN");
	var selectMonth = document.getElementById("select-month");
	var selectYear = document.getElementById("select-year");
	
	var btnDetailIn = document.getElementById("btn-detail-IN");
	var btnDetailOut = document.getElementById("btn-detail-OUT");

	function populatePeriodFilter() {
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
						document.getElementById("amount-"+module).innerHTML = amount;
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
	
	populatePeriodFilter();
	fetchCashflow(${currentMonth}, ${currentYear}, "IN");
	fetchCashflow(${currentMonth}, ${currentYear}, "OUT");
</script>
