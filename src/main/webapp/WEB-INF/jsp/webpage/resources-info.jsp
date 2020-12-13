<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="content">
	<h3>Balance Info</h3>
	<div class="row" style="grid-column-gap: 5px; grid-row-gap: 5px">

		<div class="col-5">
			<form id="form-balance">
				<div class="card">
					<div class="card-header">Filter Balance Date</div>
					<div class="card-body">
						<div style="display: grid; grid-template-columns: 30% 60%">
							<label>Day</label> <input class="form-control"
								value="${currentDay }" id="i_day" type="number" min="1" max="31" />
							<label>Month</label> <input class="form-control"
								value="${currentMonth }" id="i_month" type="number" min="1"
								max="" /> <label>Year</label> <input class="form-control"
								value="${currentYear }" id="i_year" type="number" />

						</div>
					</div>
					<div class="card-footer">
						<button class="btn btn-primary" type="submit">Submit</button>
					</div>
				</div>
			</form>
		</div>
		<div class="col-6">
			<div class="card">
				<div class="card-header">Balance</div>
				<div class="card-body">
					<div style="display: grid; grid-template-columns: 30% 60%">
						<label>Incoming</label> <input disabled="disabled"
							class="form-control" value="0" id="o_income" /> <label>Spent</label>
						<input disabled="disabled" class="form-control" value="0"
							id="o_spent" /> <label>Balance</label> <input
							disabled="disabled" class="form-control" value="0" id="o_balance" />
					</div>
				</div>
			</div>
		</div>
		<div class="col-5">
			<form id="form-inventory">
				<div class="card">
					<div class="card-header">Inventory Quantity</div>
					<div class="card-body">
						<p>Current Quantity:</p>
						<input id="current-qty" value="0" disabled="disabled"
							class="form-control" />
					</div>
					<div class="card-footer">
						<button class="btn btn-primary" type="submit">Load Quantity</button>
					</div>
				</div>
			</form>
		</div>
		<div class="col-6">
			<form id="form-memory-usage">
			<div class="card">
				<div class="card-header">Memory Usage</div>
				<div class="card-body">

					<p>Max Memory:</p>
					<input id="max-memory" value="0" disabled="disabled"
						class="form-control" />
					<p>Free Memory:</p>
					<input id="free-memory" value="0" disabled="disabled"
						class="form-control" />
					<p>Total Memory:</p>
					<input id="total-memory" value="0" disabled="disabled"
						class="form-control" />
				</div>
				<div class="card-footer">
					<button class="btn btn-primary" type="submit">Load Latest Usage</button>
				</div>
			</div>
			</form>
		</div>
	</div>
</div>
<script type="text/javascript">
	const inputDay = byId("i_day");
	const inputMonth = byId("i_month");
	const inputYear = byId("i_year");
	var URL_GET_BALANCE = "<spring:url value="/api/admin/balance" />";
	var URL_GET_INVENTORY_QTY = "<spring:url value="/api/transaction/inventoriesquantity" />";
	var URL_GET_MEMORY_USAGE = "<spring:url value="/api/admin/resourceusage" />";

	function populateBalanceInfo(balanceInfo) {
		byId("o_income").value = beautifyNominal(balanceInfo.debitAmt);
		byId("o_spent").value = beautifyNominal(balanceInfo.creditAmt);
		byId("o_balance").value = beautifyNominal(balanceInfo.actualBalance);
	}

	function showInventoryQuantity(response) {
		const qty = response.quantity;
		byId("current-qty").value = beautifyNominal(qty);
	}

	function showResourceUsage(response) {
		const memoryInfo = response.memoryInfo;
		//heapFreeSize(heapFreeSize).heapMaxSize(heapMaxSize).heapSize(heapSize).build()
		byId("max-memory").value = beautifyNominal(memoryInfo.heapMaxSize);
		byId("free-memory").value = beautifyNominal(memoryInfo.heapFreeSize);
		byId("total-memory").value = beautifyNominal(memoryInfo.heapSize);
	}

	function loadBalance() {
		const request = {
			filter : {
				day : inputDay.value,
				month : inputMonth.value,
				year : inputYear.value
			}
		};

		postReq(URL_GET_BALANCE, request, function(xhr) {
			try {
				const response = (xhr.data);
				if (response.code != "00") {
					alert("Error requesting data");
					return;
				}
				populateBalanceInfo(response.entity);
			} catch (e) {
				alert("Error requesting data");
			} finally {
				infoDone();
			}

		});
	}

	function loadInventoryQuantity() {
		postReq(URL_GET_INVENTORY_QTY, {}, function(xhr) {
			const response = (xhr.data);
			if (response.code != "00") {
				alert("Error requesting data");
				return;
			}
			showInventoryQuantity(response);
		})
	}

	function loadMemoryUsage() {
		postReq(URL_GET_MEMORY_USAGE, {}, function(xhr) {
			const response = (xhr.data);
			if (response == null) {
				alert("Error requesting data");
				return;
			}
			showResourceUsage(response);
		})
	}

	function init() {
		byId("form-balance").onsubmit = function(e) {
			e.preventDefault();
			loadBalance();
		}

		byId("form-inventory").onsubmit = function(e) {
			e.preventDefault();
			loadInventoryQuantity();
		}

		byId("form-memory-usage").onsubmit = function(e) {
			e.preventDefault();
			loadMemoryUsage();
		}
	}

	init();
</script>