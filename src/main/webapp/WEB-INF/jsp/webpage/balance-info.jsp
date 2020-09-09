<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="content">
	<h3>Balance Info</h3>
	<div class="row" style="grid-column-gap: 5px">

		<div class="row-md-6">
			<div class="card">
				<div class="card-header">Filter</div>
				<div class="card-body">
					<div style="display: grid; grid-template-columns: 30% 60%">
						<label>Day</label> <input class="form-control"
							value="${currentDay }" id="i_day" type="number" min="1" max="31" />
						<label>Month</label> <input class="form-control"
							value="${currentMonth }" id="i_month" type="number" min="1"
							max="" /> <label>Year</label> <input class="form-control"
							value="${currentYear }" id="i_year" type="number" />
						<button class="btn btn-primary" id="btn-submit">Submit</button>
					</div>
				</div>
			</div>
		</div>
		<div class="row-md-6">
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

	</div>
</div>
<script type="text/javascript">
	const inputDay = byId("i_day");
	const inputMonth = byId("i_month");
	const inputYear = byId("i_year");
	var URL_GET_BALANCE = "<spring:url value="/api/admin/balance" />";

	function populateBalanceInfo(balanceInfo) {
		byId("o_income").value = beautifyNominal(balanceInfo.debitAmt);
		byId("o_spent").value = beautifyNominal(balanceInfo.creditAmt);
		byId("o_balance").value = beautifyNominal(balanceInfo.actualBalance);
	}

	function submit() {
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

	function init() {
		byId("btn-submit").onclick = function(e) {
			submit();
		}
	}

	init();
</script>