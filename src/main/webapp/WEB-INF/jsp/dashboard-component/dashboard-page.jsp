<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="../dashboard-component/cashflow-filter.jsp"></jsp:include>
<div class="content" style="width: 100%">
 
	
	<jsp:include page="../dashboard-component/product-sales.jsp"></jsp:include>
	<jsp:include page="../dashboard-component/cashflow-history.jsp"></jsp:include>

	
	<div id="content-dashboard">
		<h2>Dashboard</h2>
		<p>Good ${timeGreeting}, ${loggedUser.displayName}. Have a great day!</p>
		<div class="input-group mb-3">
			<div class="input-group-prepend">
				<span class="input-group-text">Month</span> <select
					class="form-control" id="select-month">
					<c:forEach var="month" items="${months}">
						<option value="${month.value }" ${month.value == currentMonth ? 'selected' : '' }>${month.key }</option>
					</c:forEach>
				</select> <span class="input-group-text">Year</span> <select
					class="form-control" id="select-year">
					<c:forEach var="year" items="${years}">
						<option value="${year.value }" ${year.value == currentYear? 'selected' : '' }>${year.key }</option>
					</c:forEach>
				</select>
			</div>
			<div class="input-group-append">
				<button class="btn btn-outline-secondary" onclick="getCashflow()"><i class="fa fa-search"  ></i></button>

			</div>
		</div>
		<div>
			<button class="btn btn-outline-info" onclick="report('daily')"><i class="fa fa-print" aria-hidden="true"></i> Report
				Selected Month</button>
			<button class="btn btn-outline-info" onclick="report('monthly')"><i class="fa fa-print" aria-hidden="true"></i> Report
				Selected Year</button>
			<button class="btn btn-outline-info"
				onclick="report('balance1')"><i class="fa fa-print" aria-hidden="true"></i> Balance Report</button>
		</div>
		<p></p>
		<div class="row">

			<div class="col-md-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top" width="100" height="200"
						src="<spring:url value="/res/img/income.jpg" />"
						alt="Card image cap">
					<div class="card-body">
						<div class="card-title"><a role="button"  id="btn-detail-OUT" class="badge badge-secondary"
							link="${contextPath}/management/transaction/type=OUT"
							href="${contextPath}/management/transaction/type=OUT">
							Income <i class="fa fa-th-list" aria-hidden="true"></i></a></div>
						<ul class="list-group">
							<li
								class="list-group-item d-flex justify-content-between align-items-center">
								Selling<span class="badge badge-primary badge-pill"
								id="count-OUT">0</span>
							</li>
							<li
								class="list-group-item d-flex justify-content-between align-items-center">
								Income<br> <span id="amount-OUT">0</span>
							</li>

						</ul>
							</div>
				</div>
			</div>
			<div class="col-md-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top" width="100" height="200"
						src="<spring:url value="/res/img/wallet1.png" />"
						alt="Card image cap">
					<div class="card-body">
						<div class="card-title"><a role="button" id="btn-detail-IN" class="badge badge-secondary"
							link="${contextPath}/management/transaction/type=IN"
							href="${contextPath}/management/transaction/type=IN">Spent <i class="fa fa-th-list" aria-hidden="true"></i></a></div>
						<ul class="list-group">
							<li
								class="list-group-item d-flex justify-content-between align-items-center">
								Purchasing<span class="badge badge-primary badge-pill"
								id="count-IN">0</span>
							</li>
							<li
								class="list-group-item d-flex justify-content-between align-items-center">
								Spent<br> <span id="amount-IN">0</span>
							</li>

						</ul> 
					</div>
				</div>
			</div>
		</div>
		<p></p>
		<button class="btn btn-primary" onclick="showCashflowHistory()">Cashflow
			Detail</button>
		<button class="btn btn-primary" onclick="showProductSales()">Product
			Sales Detail</button>
		<div style="width:50%; margin-top: 10px">
			<input required="required" type="text" class="form-control" id="input-transaction-code" placeholder="Transaction Code" />
			<br/>
			<button class="btn btn-info" onclick="seeTransactionData()">See Transaction Detail</button>
		</div>
		<p></p>
	</div>
</div>
<script type="text/javascript">
	const infoSales = byId("count-OUT");
	const infoTotalIncome = byId("amount-OUT");
	const infoSpent = byId("count-IN");
	const infoTotalSpent = byId("amount-IN");  
	
	const btnDetailIn = byId("btn-detail-IN");
	const btnDetailOut = byId("btn-detail-OUT"); 

	const selectMonth = byId("select-month");
	const selectYear = byId("select-year"); 
	
	var URL_CASHFLOW_INFO = "<spring:url value="/api/transaction/cashflowinfo" />";

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
		postReq(URL_CASHFLOW_INFO  ,
				requestObject, function(xhr) {
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						let cashflow = response.entity;
						let count =0, amount = 0;
						if(cashflow != null && cashflow.count!=null && cashflow.amount!=null ){
							count = cashflow.count;
							amount = cashflow.amount;
						}
														
						byId("count-"+module).innerHTML = count;
						byId("amount-"+module).innerHTML = beautifyNominal(amount);
						byId("btn-detail-"+module).href = byId("btn-detail-"+module).getAttribute("link")
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
	 
	
	function seeTransactionData(){
		const code = byId("input-transaction-code").value;
		if(null == code || "" == code){
			return;
		}
		var url =  "<spring:url value="/admin/transactionreceipt/" />"+code;
		window.open(url,'_blank');
	}
	
	
	fetchCashflow(parseInt("${currentMonth}"), parseInt("${currentYear}"), "IN");
	fetchCashflow(parseInt("${currentMonth}"), parseInt("${currentYear}"), "OUT");
</script>
