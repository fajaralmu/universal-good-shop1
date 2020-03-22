<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="content-detail" style="display: none">
	<div id="monthly-detail-wrapper"
		style="overflow: scroll; height: 300px; padding: 5px; border: solid 1px blue">
		<div id="monthly-detail"
			style="display: grid; grid-template-columns: auto auto auto auto; padding: 5px;">

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
</div>
<script type="text/javascript">
	//detail cashflow
	var tableDetail = document.getElementById("detail-cashflow");
	var monthlyDetail = document.getElementById("monthly-detail");

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

	function loadMonthlyCashflow(month, year) {
		infoLoading();
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

		monthlyDetail.innerHTML = "<p>Date</p><p>Module</p><p>Count</p><p>Amount</p>"
				+ "<p></p><p></p><p></p><button class=\"btn btn-secondary\" onclick=\"hide('monthly-detail-wrapper')\">Close</button>";

		let detailIncome = response.monthlyDetailIncome;
		let detailCost = response.monthlyDetailCost;

		/*
			detail income
		 */
		for (let i = 1; i <= 31; i++) {
			const cashflow = detailIncome[i];
			monthlyDetail.appendChild(createLabel(i));
			monthlyDetail.appendChild(createLabel(cashflow.module));
			monthlyDetail
					.appendChild(createLabel(beautifyNominal(cashflow.count)));
			monthlyDetail
					.appendChild(createLabel(beautifyNominal(cashflow.amount)));
		}

		/*
			detail cost
		 */
		for (let i = 1; i <= 31; i++) {
			const cashflow = detailCost[i];
			monthlyDetail.appendChild(createLabel(i));
			monthlyDetail.appendChild(createLabel(cashflow.module));
			monthlyDetail.appendChild(createLabel(cashflow.count));
			monthlyDetail.appendChild(createLabel(cashflow.amount));
		}

	}
</script>