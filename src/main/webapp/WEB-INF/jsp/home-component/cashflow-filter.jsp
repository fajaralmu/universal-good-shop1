<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="filter-wrapper"
	style="display: none;">
	<div style="width:100px">
		<button  id="btn-show-filter" class="btn btn-info btn-sm"
			onclick="showFilter()">Show
			Filter</button>
		<button  style="display: none" id="btn-close-filter"
			class="btn btn-secondary  btn-sm"
			onclick="closeFilter()">Close
			Filter</button> 
	</div>
	<div class="card" id="filter-detail" hidden= "true">
		<div class="card-header">Filter</div>
		<div class="card-body">
			<p>From</p>
			<div class="grid-3-6">
				<label>Month</label> <select class="form-control"
					id="select-month-from">
					<c:forEach var="month" items="${months}">
						<option value="${month.value }">${month.key }</option>
					</c:forEach>
				</select>
			</div>
			<div class="grid-3-6"> 
				<label>Year</label> <select class="form-control"
					id="select-year-from">
					<c:forEach var="year" items="${years}">
						<option value="${year.value }">${year.key }</option>
					</c:forEach>
				</select>
			</div>
			<p>To</p>
			<div class="grid-3-6">
				<label>Month</label> <select class="form-control"
					id="select-month-to">
					<c:forEach var="month" items="${months}">
						<option value="${month.value }">${month.key }</option>
					</c:forEach>
				</select>
			</div>
			<div class="grid-3-6">
				<label>Year</label> <select class="form-control" id="select-year-to">
					<c:forEach var="year" items="${years}">
						<option value="${year.value }">${year.key }</option>
					</c:forEach>
				</select>
			</div>
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
	
	const filterForm  = _byId('filter-detail');
	const filterWrapper = _byId("filter-wrapper");
	
	function showFilter(){
		
		filterForm.removeAttribute("hidden");
		//show('filter-detail'); 
		show('btn-close-filter'); 
		hide('btn-show-filter');
		filterWrapper.style.width = '0px';
		updateWidthAsync(filterWrapper, 300, 2,null);
	}
	
	function closeFilter(){
		
		filterForm.setAttribute("hidden", "true");
		//hide('filter-detail'); 
		hide('btn-close-filter'); 
		show('btn-show-filter');
		//filterWrapper.style.width = '100px';
		updateWidthAsync(filterWrapper, 100, -2,null);
	}
</script>