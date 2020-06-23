<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="filter-detail" class="border border-primary box-shadow"
	style="padding: 10px; display: none; width: 40%; margin-right: 0px; position: fixed; background-color: white; height: auto">
	<h3>Filter</h3>
	<p>From</p>
	<label>Month</label>
	<select id="select-month-from">
		<c:forEach var="month" items="${months}">
			<option value="${month.value }">${month.key }</option>
		</c:forEach>
	</select> 
	<label>Year</label> 
	<select id="select-year-from">
		<c:forEach var="year" items="${years}">
			<option value="${year.value }">${year.key }</option>
		</c:forEach>
	</select>
	<p>To</p>
	<label>Month</label>
	<select id="select-month-to">
		<c:forEach var="month" items="${months}">
			<option value="${month.value }">${month.key }</option>
		</c:forEach>
	</select>
	<label>Year</label>
	<select id="select-year-to">
		<c:forEach var="year" items="${years}">
			<option value="${year.value }">${year.key }</option>
		</c:forEach>
	</select>

	<button id="btn-ok-filter-detail" class="btn btn-primary btn-sm">Ok</button>
	<button class="btn btn-secondary  btn-sm"
		onclick="hide('filter-detail')">Close</button>
</div>
<script type="text/javascript">
	//filter 
	var selectMonthFrom = _byId("select-month-from");
	var selectYearFrom = _byId("select-year-from");

	var selectMonthTo = _byId("select-month-to");
	var selectYearTo = _byId("select-year-to");
	
	
</script>