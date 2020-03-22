<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="content-detail" style="display: none">
	<h3>Cashflow History</h3>
	<button id="sdsjdh" class="btn btn-info"
		onclick="show('filter-detail')">Show Filter</button>
	<button class="btn btn-secondary"
		onclick="hide('content-detail');hide('filter-detail'); show('content-dashboard')">Close</button>

	<table id="detail-cashflow" class="table">

	</table>
</div>
<script type="text/javascript">
	//detail cashflow
	var tableDetail = document.getElementById("detail-cashflow");
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
	
	function showCashflowHistory(){
		document.getElementById("btn-ok-filter-detail").onclick = function(){
			
			showDetail();
		};
		show('content-detail'); hide('content-dashboard');
		show('filter-detail');
		showDetail();
	}
</script>