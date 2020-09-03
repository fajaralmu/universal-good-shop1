<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="content-detail" style="display: none;">
	<button class="btn btn-danger btn-sm" onclick="closeCashflowDetail()">Close
			Cashflow Detail</button>
	<button id="btn-show-monthly" class="btn btn-sm btn-info"
		onclick="show('monthly-detail-wrapper'); hide('btn-show-monthly')">Show
		Monthly Detail</button>
	<div id="monthly-detail-wrapper"
		style="border: solid 1px blue; border-radius: 5px; display: none">
		<div id="monthly-detail-title" style="padding: 5px;">
			<button class="btn btn-sm btn-danger"
				onclick="hide('monthly-detail-wrapper'); show('btn-show-monthly')">Close</button>
			<h3 id="title">
				Monthly Detail <small id="info-period"></small>
			</h3>
			<p>Click the row to see detail selling</p>
		</div>
		<div style="overflow: scroll; overflow-x: hidden; height: 300px;">
			<div id="monthly-detail"
				style="padding: 5px; background-color: lightgray;"></div>
		</div>
	</div>
	<div id="main-detail">
		<h3>Cashflow History</h3> 

		<table id="detail-cashflow" class="table">

		</table>
	</div>

</div>
<script type="text/javascript">
	//detail cashflow
	const tableDetail = _byId("detail-cashflow");
	const monthlyDetail = _byId("monthly-detail");
	const infoDetailPeriod = _byId("info-period");

	var responseDetailMonthly = {};
	var responseDetailDaily = {};
	var selectedMonth = 0;
	var selectedYear = 0;
	var selectedDay = 0;

	function closeCashflowDetail() {
		if(!confirm("Close This Menu?")) return;
		
		hide('content-detail');
		hide('filter-detail');
		show('content-dashboard')
	}

	function showDetail() {

		const requestObject = {
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
					const supplies = xhr.data.supplies;
					const maxValue = xhr.data.maxValue;
					const purchases = xhr.data.purchases;
					const tableColumns = [ [
							"No setting= <style>width:50px</style>",
							"Period setting= <style>width:60px</style>",
							"Amount setting=<colspan>6</colspan>" ]

					];
					for (let i = 0; i < supplies.length; i++) {
						const cashflowSupplies = supplies[i];
						const cashflowPurchases = purchases[i];

						const percentSupplies = ((cashflowSupplies.amount / maxValue) * 100)
								+ "%";
						const percentPurchases = ((cashflowPurchases.amount / maxValue) * 100)
								+ "%";

						const month = cashflowSupplies.month;
						const year = cashflowSupplies.year;

						const chartLegend = "<span class=\"badge badge-warning\">"
								+ beautifyNominal(cashflowSupplies.amount)
								+ " ("
								+ beautifyNominal(cashflowSupplies.count)
								+ " unit)</span>"
								+ "<span class=\"badge badge-primary\">"
								+ beautifyNominal(cashflowPurchases.amount)
								+ " ("
								+ beautifyNominal(cashflowPurchases.count)
								+ " unit)</span> setting= <style>width:200px</style>";

						const chartBody = //supply
						"<div class=\"rounded-right chart-item-hr\" "+
							"style=\" width:"+percentSupplies+"; font-size:0.7em; background-color:orange\">"
								+ "</div>"
								//purchase
								+ "<div class=\"rounded-right chart-item-hr\" "
								+"style=\" width:"+percentPurchases+"; font-size:0.7em; background-color:green\">"
								+ "</div>" + "setting= <colspan>5</colspan>";

						const columns = [
								i + 1,
								"<a href=\"#\" class=\"badge badge-info\" onclick=\"loadMonthlyCashflow("
										+ month + "," + year + ")\"> "
										+ (monthNames[month - 1] + "-" + year)
										+ "</a>", chartLegend, chartBody ];

						tableColumns.push(columns);
					}
					const tbody = createTBodyWithGivenValue(tableColumns);
					tableDetail.innerHTML = tbody.innerHTML;
				});

	}

	function showCashflowHistory() {
		_byId("btn-ok-filter-detail").onclick = function() {

			showDetail();
		};
		show('content-detail');
		hide('content-dashboard');
		show('filter-wrapper');
		showDetail();
	}

	function setRowLikeElement(el) {
		if (!el)
			return;
		el.style.backgroundColor = '#ffffff';
		el.style.marginBottom = '3px';
		el.style.borderRadius = '5px';
	}

	function loadDailyCashflow(day, month, year) {
		infoLoading();
		selectedDay = day;

		const requestObject = {
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
						window['responseDetailDaily'] = response;
						populateDailyDetail();
					} else {
						alert("Failed getting cashflow: ");
					}
					infoDone();
				});
	}

	function loadMonthlyCashflow(month, year) {
		infoLoading();

		selectedMonth = month;
		selectedYear = year;

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
						window['responseDetailMonthly'] = response;
						populateMonthlyDetail();
					} else {
						alert("Failed getting cashflow: ");
					}
					infoDone();
				});
	}

	function populateDailyDetail() {
		monthlyDetail.innerHTML = "";
		infoDetailPeriod.innerHTML = selectedDay + " "
				+ monthNames[selectedMonth - 1] + " " + selectedYear;

		const btnClose = createButton("btn-close", "Back");
		btnClose.className = "btn";
		btnClose.onclick = function() {
			populateMonthlyDetail();
		}

		const columnTitleWrapper = createGridWrapper(3);
		columnTitleWrapper.innerHTML = "<p><b>Product</b></p><p><b>Sold Quantity</b></p><p><b>Total Price</b></p>";
		setRowLikeElement(columnTitleWrapper);

		monthlyDetail.appendChild(btnClose);
		monthlyDetail.appendChild(columnTitleWrapper);

		const dailyIncome = responseDetailDaily.dailyCashflow;
		let number = 1;

		/**
			detail  
		 */
		for ( var key in dailyIncome) {

			const cashflow = dailyIncome[key];

			const rowWrapper = createGridWrapper(3, "30%");
			rowWrapper.setAttribute("class", "left-aligned");
			setRowLikeElement(rowWrapper);

			const productCount = createDiv(key, "chart-item");
			productCount.style.backgroundColor = 'lightGreen';
			productCount.style.width = cashflow.proportion + "%";
			productCount.style.height = '13px';
			productCount.style.fontSize = '0.7em';
			productCount.style.margin = '3px';
			productCount.innerHTML = beautifyNominal(cashflow.count);

			rowWrapper.appendChild(createLabel(cashflow.product ? number + ". "
					+ cashflow.product.name : ""));
			rowWrapper.appendChild(productCount);
			rowWrapper.appendChild(nominalLabelBeautified(cashflow.amount));

			monthlyDetail.appendChild(rowWrapper);

			number++;
		}
	}

	function populateMonthlyDetail() {

		monthlyDetail.innerHTML = "";
		infoDetailPeriod.innerHTML = monthNames[selectedMonth - 1] + " "
				+ selectedYear;

		const thWrapper = createGridWrapper(4);
		thWrapper.innerHTML = "<p><b>Date</b></p><p><b>Module</b></p><p><b>Quantity</b></p><p><b>Amount</b></p>";
		thWrapper.style.textAlign = 'center';
		setRowLikeElement(thWrapper);

		monthlyDetail.appendChild(thWrapper);

		const detailIncome = responseDetailMonthly.monthlyDetailIncome;
		const detailCost = responseDetailMonthly.monthlyDetailCost;

		/*
			detail income
		 */
		for (let i = 1; i <= 31; i++) {

			const rowWrapper = createGridWrapper(4, "20%");
			rowWrapper.setAttribute("class", "clickable center-aligned");
			setRowLikeElement(rowWrapper);
			/*
				cash
			 */
			const cashflow = detailIncome[i];

			rowWrapper.appendChild(createLabel(i));
			rowWrapper.appendChild(createLabel(cashflow.module));
			rowWrapper.appendChild(nominalLabelBeautified(cashflow.count));
			rowWrapper.appendChild(nominalLabelBeautified(cashflow.amount));

			/*
				cost
			 */
			const costFlow = detailCost[i];

			rowWrapper.appendChild(createLabel(""));
			rowWrapper.appendChild(createLabel(costFlow.module));
			rowWrapper.appendChild(nominalLabelBeautified(costFlow.count));
			rowWrapper.appendChild(nominalLabelBeautified(costFlow.amount));

			rowWrapper.onclick = function() {
				loadDailyCashflow(i, selectedMonth, selectedYear);
			}

			monthlyDetail.appendChild(rowWrapper);
		}

		show('monthly-detail-wrapper');
		hide('btn-show-monthly');

	}

	function nominalLabelBeautified(value) {
		return createLabel(beautifyNominal(value));
	}
</script>