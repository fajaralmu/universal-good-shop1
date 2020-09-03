<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="content" style="width: 100%">


	<jsp:include page="../home-component/cashflow-filter.jsp"></jsp:include>
	<jsp:include page="../home-component/product-sales.jsp"></jsp:include>
	<jsp:include page="../home-component/cashflow-history.jsp"></jsp:include>


	<div id="content-dashboard">
		<h2>Dashboard</h2>
		<p>Good ${timeGreeting}, ${loggedUser.displayName}. Have a great
			day!</p>
		<div class="input-group mb-3">
			<div class="input-group-prepend">
				<span class="input-group-text">Month</span> <select
					class="form-control" id="select-month">
					<c:forEach var="month" items="${months}">
						<option value="${month.value }">${month.key }</option>
					</c:forEach>
				</select> <span class="input-group-text">Year</span> <select
					class="form-control" id="select-year">
					<c:forEach var="year" items="${years}">
						<option value="${year.value }">${year.key }</option>
					</c:forEach>
				</select>
			</div>
			<div class="input-group-append">
				<button class="btn btn-outline-secondary" onclick="getCashflow()">Search</button>

			</div>
		</div>
		<div>
			<button class="btn btn-outline-info" onclick="report('daily')">Generate Report
				This Month</button>
			<button class="btn btn-outline-info" onclick="report('monthly')">Generate Report
				This Year</button>
			<button class="btn btn-outline-info"
				onclick="report('balance1')">Generate Balance Report</button>
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
		<button class="btn btn-primary" onclick="showCashflowHistory()">Cashflow
			Detail</button>
		<button class="btn btn-primary" onclick="showProductSales()">Product
			Sales Detail</button>
		<p></p>
	</div>
</div>
<script type="text/javascript">
	var infoSales = _byId("count-OUT");
	var infoTotalIncome = _byId("amount-OUT");
	var infoSpent = _byId("count-IN");
	var infoTotalSpent = _byId("amount-IN");  
	
	var btnDetailIn = _byId("btn-detail-IN");
	var btnDetailOut = _byId("btn-detail-OUT"); 

	var selectMonth = _byId("select-month");
	var selectYear = _byId("select-year"); 

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
														
						_byId("count-"+module).innerHTML = count;
						_byId("amount-"+module).innerHTML = beautifyNominal(amount);
						_byId("btn-detail-"+module).href = _byId("btn-detail-"+module).getAttribute("link")
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
	
	function report(type){
		infoLoading();
		var requestObject = {
				"filter":{
					"year":selectYear.value,
					"month":selectMonth.value 
				}
		};
		postReq("<spring:url value="/api/report/" />" +type ,
				requestObject, function(xhr) {
			
			downloadFileFromResponse(xhr);
			
				infoDone();
				}, true);
	} 
	 
	
	
	fetchCashflow(${currentMonth}, ${currentYear}, "IN");
	fetchCashflow(${currentMonth}, ${currentYear}, "OUT");
</script>
