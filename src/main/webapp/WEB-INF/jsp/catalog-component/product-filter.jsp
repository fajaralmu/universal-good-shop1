<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="filter-wrapper">
	<div style="display: grid; grid-template-columns: 55px 300px">
		<div style="width: 50px; z-index: 1; margin-top: 28px">
			<button id="btn-show-filter"
				class="btn btn-info btn-sm btn-filter-toggle" onclick="showFilter()">Show
				Filter</button>
			<button style="display: none" id="btn-close-filter"
				class="btn btn-secondary  btn-sm  btn-filter-toggle"
				onclick="closeFilter()">Close Filter</button>
		</div>
		<div class="card" id="filter-detail" hidden="true"
			style="width: 300px">
			<div class="card-header">Filter</div>
			<div class="card-body">
				<form id="product-filter-form" onsubmit="event.preventDefault(); loadEntity();">
					<p>Name</p>
					<input id="search-name" class="form-control" />
					<p>Category</p>
					<select class="form-control" id="select-category">
						<option value="00">All</option>
						<c:forEach var="category" items="${categories }">
							<option value="${category.id }">${category.name }</option>
						</c:forEach>
					</select>
					<p>Order By</p>
					<select class="form-control" id="select-order">
						<option value="00" selected="selected">NONE</option>
						<option value="name-asc">Name [A-Z]</option>
						<option value="name-desc">Name [Z-A]</option>
						<option value="price-asc">Price [cheap]</option>
						<option value="price-desc">Price [expensive]</option>
					</select>
					<p>
						<input type="checkbox" id="get-stock"
							aria-label="Checkbox for following text input"> <span>Include
							Stock</span>
					</p>
					<p>Display per Page</p>
					<select class="form-control" id="select-limit">
						<option value="10" selected="selected">10</option>
						<option value="15">15</option>
						<option value="20">20</option>

					</select>
					<p></p>
					<button class="btn btn-primary btn-sm" type="submit">Search</button>
					<input type="reset" class="btn btn-warning btn-sm"/>
				</form>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	//filter  
	const filterForm = byId('filter-detail');
	const filterWrapper = byId("filter-wrapper");

	function showFilter() {

		filterForm.removeAttribute("hidden");
		//show('filter-detail');  
		filterWrapper.style.width = '55px';
		updateWidthAsync(filterWrapper, 350, 3, function() {
			show('btn-close-filter');
			hide('btn-show-filter');
		});
	}

	function closeFilter() {

		//hide('filter-detail'); 

		//filterWrapper.style.width = '100px';
		updateWidthAsync(filterWrapper, 55, -3, function() {
			filterForm.setAttribute("hidden", "true");
			hide('btn-close-filter');
			show('btn-show-filter');
		});

	}
</script>