<%@page import="com.fajar.entity.User"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<style>
.card-title:hover {
	cursor: pointer;
}
</style>
<div class="content">
	<p></p>
	<!-- Detail Product Supplier -->
	<div class="modal fade" id="modal-product-suppliers" tabindex="-1"
		role="dialog" aria-labelledby="Product Suppliers" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered modal-lg"
			role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="title-detail-modal">Supplier</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body"
					style="width: 90%; height: 400px; margin: auto; overflow: scroll;">
					<table class="table" id="table-supplier-list" style="layout: fixed">
					</table>
					<div style="text-align: center">
						<button class="btn btn-outline-success" onclick="loadMoreSupplier()">More</button>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Detail Product -->
	<div id="detail-content" class="row"
		style="width: 95%; margin: auto; display: none">
		<table class="table" style="layout: fixed;">
			<tr>
				<td style="width: 60%">
					<button class="btn btn-primary btn-sm" id="close-detail"
						onclick="closeDetail()">Back</button>
				</td>
				<td style="width: 40%">
					<h2 id="product-title"></h2>

				</td>
			</tr>
			<tr valign="top">
				<td style="width: 60%">

					<div id="carousel-wrapper" style="width: 100%; margin: auto">
						<div id="carouselExampleIndicators" class="carousel slide"
							data-ride="carousel">
							<ol id="carousel-indicators" class="carousel-indicators">
							</ol>
							<div id="carousel-inner" class="carousel-inner"></div>
							<a class="carousel-control-prev"
								href="#carouselExampleIndicators" role="button"
								data-slide="prev"> <span class="carousel-control-prev-icon"
								aria-hidden="true"></span> <span class="sr-only">Previous</span>
							</a> <a class="carousel-control-next"
								href="#carouselExampleIndicators" role="button"
								data-slide="next"> <span class="carousel-control-next-icon"
								aria-hidden="true"></span> <span class="sr-only">Next</span>
							</a>
						</div>
					</div>
				</td>
				<!--  -->
				<td style="width: 40%">
					<!-- END CAROUSEL -->

					<ul class="list-group">
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							Stock <span class="badge badge-primary badge-pill"
							id="product-stock">0</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							Price<br> <span id="product-price">0</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							Unit<br> <span id="product-unit">0</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							Category<br> <span id="product-category">0</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center">
							<button class="btn btn-primary" onclick="showproductsuppliers()">Supplier
								List</button>
						</li>


					</ul>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<p>Description</p>
					<p id="product-description">0</p>
				</td>
			</tr>
		</table>
	</div>
	
	<!-- Catalog of Products -->
	
	<div id="catalog-content">
		<h2>Product Catalog</h2>
		<p></p>

		<table
			style="layout: fixed; border-collapse: separate; border-spacing: 5px;">
			<tr valign="top" style="width: 100%">
				<td style="width: 20%">
					<!-- FILTER -->
					<div>
						<p>Name</p>
						<input id="search-name" class="form control" />
						<p>Category</p>
						<select class="form control" id="select-category">
							<option value="00">All</option>
							<c:forEach var="category" items="${categories }">
								<option value="${category.id }">${category.name }</option>
							</c:forEach>
						</select>
						<p>Order By</p>
						<select class="form control" id="select-order">
							<option value="00" selected="selected">NONE</option>
							<option value="name-asc">Name [A-Z]</option>
							<option value="name-desc">Name [Z-A]</option>
							<option value="price-asc">Price [cheap]</option>
							<option value="price-desc">Price [expensive]</option>
						</select> 
						<p>
							<input class="form control"   type="checkbox"
								id="get-stock" aria-label="Checkbox for following text input">
							<span>Include Stock</span>
						</p>
						<p>Display per Page</p>
						<select class="form control" id="select-limit">
							<option value="10" selected="selected">10</option>
							<option value="15">15</option>
							<option value="20">20</option>
							 
						</select> 
					</div>
					<button class="btn btn-outline-primary" onclick="loadEntity()">Search</button>
				</td>
				<td style="width: 80%">
					<!-- PAGINATION -->
					<nav>
						<ul class="pagination" id="navigation-panel"></ul>
					</nav>
					<div id="catalog-panel" class="row"></div>
				</td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">


	var page = 0;
	var limit = 8;
	var totalData = 0;
	var orderBy = null;
	var orderType = null;
	var defaultOption = "${defaultOption}";
	
	//filted
	var selectOrder = _byId("select-order");

	//elements
	var navigationPanel = _byId("navigation-panel");
	var catalogPanel = _byId("catalog-panel");
	var nameFilter = _byId("search-name");
	var chkBoxGetStock = _byId("get-stock");
	var categoryFilter = _byId("select-category");
	var tableSupplierList = _byId("table-supplier-list");

	//detail
	var productTitle = _byId("product-title");
	var productUnit = _byId("product-unit");
	var productCategory = _byId("product-category");
	var productDescription = _byId("product-description");
	var carouselInner = _byId("carousel-inner");
	var carouselIndicator = _byId("carousel-indicators");
	var defaultLocation = window.location.href;
	var supplierOffset = 0;
	var selectedProductId = 0;

	function showproductsuppliers() {
		$('#modal-product-suppliers').modal('show');
	}

	function closeDetail() {
		hide("detail-content");
		show("catalog-content");
		window.history.pushState('catalog-page', 'Product Catalog',
				defaultLocation);
		this.supplierOffset = 0;
		this.selectedProductId = 0;
	}

	

	function loadMoreSupplier() {
		supplierOffset++;
		doLoadMoreSupplier(supplierOffset, selectedProductId);
	}

	function doLoadMoreSupplier(offset, productId) {
		doLoadEntities("<spring:url value="/api/public/moresupplier" />", {
			"filter" : {
				"page" : offset,
				"fieldsFilter" : {
					"productId" : productId
				}
			}
		}, function(response) {
			var entities = response.entities;
			if (entities != null && entities.length > 0) {
				let bodyRows = createTableBody(
						[ "name", "website", "address" ], entities,
						(offset * 5));

				for (var i = 0; i < bodyRows.length; i++) {
					let row = bodyRows[i];
					tableSupplierList.append(row);
				}
			} else
				alert("Data Not Found");
			infoDone();
		});
	}

	function populateDetail(entity) {
		selectedProductId = entity.id;
		console.log("entity", entity);
		hide("catalog-content");

		//POPULATE

		//title, count, price
		productTitle.innerHTML = entity.name;
		_byId("product-stock").innerHTML = entity.count;
		_byId("product-price").innerHTML = beautifyNominal(entity.price);
		productUnit.innerHTML = entity.unit.name;
		productCategory.innerHTML = entity.category.name;
		productDescription.innerHTML = entity.description;
		//image
		carouselInner.innerHTML = "";
		carouselIndicator.innerHTML = "";

		let images = entity.imageUrl.split("~");
		for (var i = 0; i < images.length; i++) {
			let imageUrl = images[i];

			//indicator
			let className = null;
			if (i == 0) {
				className = "active";
			}
			let li = createElement("li", "indicator-" + i, className);
			li.setAttribute("data-slide-to", "" + i);
			li.setAttribute("data-target", "#carouselExampleIndicators");
			carouselIndicator.append(li);

			//inner
			let innerDiv = createDiv("item-" + i, "carousel-item " + className);
			let src = "${host}/${contextPath}/${imagePath}/" + imageUrl;
			let iconImage = createImgTag("icon-" + entity.id + "-" + i,
					"d-block w-100  ", "300", "300", src);
			iconImage.setAttribute("alt", entity.name + "-" + i);

			innerDiv.append(iconImage);
			carouselInner.append(innerDiv);
		}

		//suppliers
		let suppliers = entity.suppliers;

		tableSupplierList.innerHTML = "";
		let tableHeader = createTableHeaderByColumns([ "name", "website",
				"address" ]);
		let bodyRows = createTableBody([ "name", "website", "address" ],
				suppliers);
		tableSupplierList.append(tableHeader);
		for (var i = 0; i < bodyRows.length; i++) {
			let row = bodyRows[i];
			tableSupplierList.append(row);
		}

		var slash = "";
		if (!window.location.href.endsWith("/"))
			slash = "/";
		if (defaultOption == "")
			window.history.pushState('detail-page', entity.name,
					window.location.href + slash + entity.code);
		defaultOption = "";
		show("detail-content");

	}

	function loadDetail(code) {
		infoLoading();
		var requestObject = {
			"entity" : "product",
			"filter" : {
				"limit" : 1,
				"exacts" : true,
				"contains" : false,
				"fieldsFilter" : {
					"code" : code,
					"withStock" : true,
					"withSupplier" : true
				}
			}
		};
		doLoadEntities("<spring:url value="/api/public/get" />", requestObject,
				function(response) {
					var entities = response.entities;
					if (entities != null && entities.length > 0)
						populateDetail(entities[0]);
					else
						alert("Data Not Found");
					infoDone();
				});
	}

	function populateCatalog(entities) {
		catalogPanel.innerHTML = "";
		for (let i = 0; i < entities.length; i++) {
			let entity = entities[i];

			//create col
			let colDiv = createDiv("col-" + entity.id, "col-sm-3");
			//create card
			let cardDiv = createDiv("card-" + entity.id, "card");
			cardDiv.style.width = "100%";
			cardDiv.style.backgroundColor = entity.color;
			cardDiv.style.color = entity.fontColor;
			//create icon tag
			let imageUrl = entity.imageUrl;

			let src = "${host}/${contextPath}/${imagePath}/"
					+ imageUrl.split("~")[0];
			let iconImage = createImgTag("icon-" + entity.id, "card-img-top",
					"100", "150", src);
			iconImage.setAttribute("alt", entity.name);

			//card body
			let cardBody = createDiv("card-body-" + entity.id, "card-body");
			/* <div class="card-body"> */
			//card  title
			let cardTitle = createHeading("h5", "title-" + entity.id,
					"card-title", entity.name + " <small style=\"background-color:rgb(224,224,224)\" class=\"text-muted\">"
							+ (entity.newProduct?"(NEW)":"")+ "</small>");
			cardTitle.onclick = function() {
				loadDetail(entity.code);
			}

			//list group
			let listGroup = createElement("ul", "list-group-" + entity.id,
					"list-group");
			listGroup.style.color = "#000000";
			//item list #1
			let listItemCount = createElement("li", "list-item-count-"
					+ entity.id,
					"list-group-item d-flex justify-content-between align-items-center");
			listItemCount.innerHTML = "Stock<br> <span class=\"badge badge-primary badge-pill\" "+
			" id=\"product-count-"+entity.id+"\">"
					+ entity.count + "</span>";

			//item list #2
			let listItemPrice = createElement("li", "list-item-price-"
					+ entity.id,
					"list-group-item d-flex justify-content-between align-items-center");
			listItemPrice.innerHTML = "Price<br> <span class=\"text-warning\" id=\"product-price-"+entity.id+"\">"
					+ beautifyNominal(entity.price) + "</span>";
			listGroup.append(listItemCount);
			listGroup.append(listItemPrice);
			
			<% if(request.getSession().getAttribute("user")  != null && request.getSession().getAttribute("user") instanceof User) {%>
				let listItemDetailLink = createElement("li", "list-item-detail-link-"
					+ entity.id,
					"list-group-item d-flex justify-content-between align-items-center");
				listItemDetailLink.innerHTML = "<a href=\"<spring:url value="/admin/product/" />"+entity.code+"\">setting</a>";
				listGroup.append(listItemDetailLink);
			<%}	%>

			//populate cardbody
			cardBody.append(cardTitle);
			cardBody.append(listGroup);
			let categoryTag = createHeading("h5","category-"+entity.id, "", "<span class=\"badge badge-secondary\">"+entity.category.name+"</span>" );
			cardBody.append(categoryTag);
			//populate overall card
			cardDiv.append(iconImage);
			cardDiv.append(cardBody);

			//populate column
			colDiv.append(cardDiv);
			catalogPanel.append(colDiv);
		}
	}

	function loadEntity(page) {
		this.limit = _byId("select-limit").value;
		if(this.limit > 20 || this.limit < 0){
			alert("Woooww.. our server will be confused with your choice");
			this.limit = 10;
			return;
		}
		if (page < 0) {
			page = this.page;
		}
		var selectedOrder = selectOrder.value;
		if(selectedOrder != null && selectedOrder != "00"){
			this. orderBy = selectedOrder.split("-")[0];
			this.orderType = selectedOrder.split("-")[1];
		}else{
			this. orderBy  = null;
			this.orderType = null;
		}
		
		var requestObject = {
			"entity" : "product",
			"filter" : {
				"limit" : this.limit,
				"page" : page,
				"orderBy" : orderBy,
				"orderType" : orderType,
				"fieldsFilter" : {
					"name" : nameFilter.value,
					"withStock" : chkBoxGetStock.checked
				}
			}
		};
		if (categoryFilter.value != "00") {
			requestObject["filter"]["fieldsFilter"]["category,id[EXACTS]"] = categoryFilter.value;
		}

		doLoadEntities("<spring:url value="/api/public/get" />", requestObject,
				function(response) {
					if(response.code != "00"){
						alert("Data Not Found");
						return;
					}
					var entities = response.entities;
					totalData = response.totalData;
					this.page = response.filter.page;
					populateCatalog(entities);
					updateNavigationButtons();
				});

	}

	function updateNavigationButtons() {
		createNavigationButtons(this.navigationPanel, this.page,
				this.totalData, this.limit, this.loadEntity);
	}

	if (defaultOption != "") {
		loadDetail(defaultOption);
		defaultLocation = defaultLocation.replace(defaultOption, "");
		defaultLocation = defaultLocation.replace("/" + defaultOption, "");
	}

	loadEntity(page);
</script>
