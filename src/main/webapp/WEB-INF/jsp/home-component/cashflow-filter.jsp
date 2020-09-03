<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="filter-wrapper"
	style="display: none; padding: 10px; width: 40%; margin-right: 0px; background-color: white; height: auto">
	<button id="btn-show-filter" class="btn btn-info btn-sm"
		onclick="show('filter-detail'); show('btn-close-filter'); hide('btn-show-filter')">Show
		Filter</button>
	<button style="display: none" id="btn-close-filter"
		class="btn btn-secondary  btn-sm"
		onclick="hide('filter-detail'); hide('btn-close-filter'); show('btn-show-filter')">Close
		Filter</button>


	<div class="card" id="filter-detail" style="display: none;">
		<div class="card-header">Filter</div>
		<div class="card-body">
			<p>From</p>
			<label>Month</label> <select class="form-control" id="select-month-from">
				<c:forEach var="month" items="${months}">
					<option value="${month.value }">${month.key }</option>
				</c:forEach>
			</select> <label>Year</label> <select class="form-control" id="select-year-from">
				<c:forEach var="year" items="${years}">
					<option value="${year.value }">${year.key }</option>
				</c:forEach>
			</select>
			<p>To</p>
			<label>Month</label> <select class="form-control" id="select-month-to">
				<c:forEach var="month" items="${months}">
					<option value="${month.value }">${month.key }</option>
				</c:forEach>
			</select> <label>Year</label> <select class="form-control" id="select-year-to">
				<c:forEach var="year" items="${years}">
					<option value="${year.value }">${year.key }</option>
				</c:forEach>
			</select>
			<button id="btn-ok-filter-detail" class="btn btn-primary btn-sm">OK</button>
		</div>
	</div>
</div>
<script type="text/javascript">
	//filter 
	var selectMonthFrom = _byId("select-month-from");
	var selectYearFrom = _byId("select-year-from");

	var selectMonthTo = _byId("select-month-to");
	var selectYearTo = _byId("select-year-to");
</script>