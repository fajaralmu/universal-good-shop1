<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="content-detail" style="display: none;">
	<button id="btn-show-monthly" class="btn btn-sm btn-secondary"
		onclick="show('monthly-detail-wrapper'); hide('btn-show-monthly')">Show
		Monthly Detail</button>
	<div id="monthly-detail-wrapper"
		style="border: solid 1px blue; display: none">
		<div id="monthly-detail-title" style="padding: 5px;">
			<h3 id="title">Monthly Detail</h3>
			<button class="btn btn-sm btn-secondary"
				onclick="hide('monthly-detail-wrapper'); show('btn-show-monthly')">Close</button>
		</div>
		<div style="overflow: scroll; height: 300px;">
			<div id="monthly-detail" style="padding: 5px;"></div>
		</div>
	</div>
	<div id="main-detail">
		<h3>Cashflow History</h3>
		<button id="sdsjdh" class="btn btn-info"
			onclick="show('filter-detail')">Show Filter</button>
		<button class="btn btn-secondary"
			onclick="hide('content-detail');hide('filter-detail'); show('content-dashboard')">Close</button>

		<table id="detail-cashflow" class="table">

		</table>
	</div>

</div>
<script type="text/javascript">
	//detail cashflow
	var tableDetail = document.getElementById("detail-cashflow");
	var monthlyDetail = document.getElementById("monthly-detail");
	
	var selectedMonth = 0;
	var selectedYear = 0;

	function showDetail() {

		var requestObject = {
			"filter" : {
				"month" : selectMonthFrom.value,
				"year" : selectYearFrom.value,
				"monthTo" : selectMonthTo.value,
				"yearTo" : selectYearTo.value
			}
		};
		tableDetail.innerHTML = "";
		postReq(
				"<spring:url value="/api/transaction/cashflowdetail" />",
				requestObject,
				function(xhr) {
					var supplies = xhr.data.supplies;
					var maxValue = xhr.data.maxValue;
					var purchases = xhr.data.purchases;
					var tableColumns = [ [
							"No setting= <style>width:50px</style>",
							"Period setting= <style>width:60px</style>",
							"Amount setting=<colspan>6</colspan>" ]

					];
					for (let i = 0; i < supplies.length; i++) {
						let cashflowSupplies = supplies[i];
						let cashflowPurchases = purchases[i];

						let percentSupplies = ((cashflowSupplies.amount / maxValue) * 100)
								+ "%";
						let percentPurchases = ((cashflowPurchases.amount / maxValue) * 100)
								+ "%";

						let month = cashflowSupplies.month;
						let year = cashflowSupplies.year;

						let chartLegend = "<p style=\" font-size:0.8em;text-align:right\">"
								+ beautifyNominal(cashflowSupplies.amount)
								+ " ("
								+ beautifyNominal(cashflowSupplies.count)
								+ " unit)</p>"
								+ "<p style=\" font-size:0.8em;text-align:right\">"
								+ beautifyNominal(cashflowPurchases.amount)
								+ " ("
								+ beautifyNominal(cashflowPurchases.count)
								+ " unit)</p> setting= <style>width:200px</style>";

						let chartBody = //supply
						"<div class=\"rounded-right\" "+
							"style=\" margin:5px; height: 20px;width:"+percentSupplies+"; font-size:0.7em; background-color:orange\">"
								+ "</div>"
								+
								//purchase
								"<div class=\"rounded-right\" "+
							"style=\" margin:5px; height: 20px;width:"+percentPurchases+"; font-size:0.7em; background-color:green\">"
								+ "</div>" + "setting= <colspan>5</colspan>";

						let columns = [
								i + 1,
								"<span class=\"clickable\" onclick=\"loadMonthlyCashflow("
										+ month + "," + year + ")\"> " + month
										+ "-" + year + "</span>", chartLegend,
								chartBody ];

						tableColumns.push(columns);
					}
					let tbody = createTBodyWithGivenValue(tableColumns);
					tableDetail.innerHTML = tbody.innerHTML;
				});

	}

	function showCashflowHistory() {
		document.getElementById("btn-ok-filter-detail").onclick = function() {

			showDetail();
		};
		show('content-detail');
		hide('content-dashboard');
		show('filter-detail');
		showDetail();
	}

	function loadDailyCashflow(day, month, year) {
		infoLoading();
		var requestObject = {
			"filter" : {
				"year" : year,
				"month" : month,
				"day" : day
			}
		};

		postReq("<spring:url value="/api/transaction/dailycashflow" />",
				requestObject, function(xhr) {
					var response = (xhr.data);
					if (response != null && response.code == "00") {

					} else {
						alert("Failed getting cashflow: ");
					}
					infoDone();
				});
	}

	function loadMonthlyCashflow(month, year) {
		infoLoading();
		
		selectedMonth	= month;
		selectedYear	= year;
		
		var requestObject = {
			"filter" : {
				"year" : year,
				"month" : month
			}
		};

		postReq("<spring:url value="/api/transaction/monthlycashflow" />",
				requestObject, function(xhr) {
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						populateMonthlyDetail(response);
					} else {
						alert("Failed getting cashflow: ");
					}
					infoDone();
				});
	}

	function populateMonthlyDetail(response) {

		monthlyDetail.innerHTML = "";

		let thWrapper = createGridWrapper(4);
		thWrapper.innerHTML = "<p>Date</p><p>Module</p><p>Count</p><p>Amount</p>";

		monthlyDetail.appendChild(thWrapper);

		let detailIncome = response.monthlyDetailIncome;
		let detailCost = response.monthlyDetailCost;

		/*
			detail income
		 */
		for (let i = 1; i <= 31; i++) {
			
			const rowWrapper = createGridWrapper(4, "20%");
			rowWrapper.setAttribute("class", "clickable center-aligned");

			
			/*
				cash
			*/
			
			const cashflow = detailIncome[i]; 

			rowWrapper.appendChild(createLabel(i));
			rowWrapper.appendChild(createLabel(cashflow.module));
			rowWrapper
					.appendChild(createLabel(beautifyNominal(cashflow.count)));
			rowWrapper
					.appendChild(createLabel(beautifyNominal(cashflow.amount)));
			
			/*
				cost
			*/
			const costFlow = detailCost[i]; 

			rowWrapper.appendChild(createLabel(""));
			rowWrapper.appendChild(createLabel(costFlow.module));
			rowWrapper
					.appendChild(createLabel(beautifyNominal(costFlow.count)));
			rowWrapper
					.appendChild(createLabel(beautifyNominal(costFlow.amount)));
			
			
			rowWrapper.onclick = function(){
				loadDailyCashflow(i, selectedMonth, selectedYear);
			}

			monthlyDetail.appendChild(rowWrapper);
		}

		show('monthly-detail-wrapper');
		hide('btn-show-monthly');

	}
</script>