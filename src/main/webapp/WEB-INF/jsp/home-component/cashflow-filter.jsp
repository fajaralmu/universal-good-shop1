<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="filter-detail" class="border border-primary box-shadow"
	style="padding: 10px; display: none; width: 40%; margin-right: 0px; position: fixed; background-color: white; height: auto">
	<h3>Filter</h3>
	<p>From</p>
	Month <select id="select-month-from"></select> Year <select
		id="select-year-from"></select>
	<p>To</p>
	Month <select id="select-month-to"></select> Year <select
		id="select-year-to"></select>

	<button id="btn-ok-filter-detail" class="btn btn-primary btn-sm">Ok</button>
	<button class="btn btn-secondary  btn-sm"
		onclick="hide('filter-detail')">Close</button>
</div>
<script type="text/javascript">
	//filter 
	var selectMonthFrom = document.getElementById("select-month-from");
	var selectYearFrom = document.getElementById("select-year-from");

	var selectMonthTo = document.getElementById("select-month-to");
	var selectYearTo = document.getElementById("select-year-to");
	
	
</script>