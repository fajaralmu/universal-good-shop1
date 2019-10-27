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

	<div id="detail-content" class="row" style="display: none">
		<table class="table" style="layout:fixed">
			<tr>
				<td style="width:60%">
					<button class="btn btn-primary btn-sm" id="close-detail"
							onclick="closeDetail()">Back</button>
				</td>
				<td style="width:40%">
					<h2 id="product-title"></h2>
						
				</td>
			</tr>
			<tr valign="top">
				<td style="width:60%">
					
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
				<td style="width:40%">
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
	<div id="catalog-content">
		<h2>Product Catalog</h2>
		<p></p>
		<!-- FILTER -->

		<div class="input-group mb-3">
			<div class="input-group-prepend">
				<span class="input-group-text">Name</span>
			</div>
			<input id="search-name" class="form control" />
			<div class="input-group-append">
				<button class="btn btn-outline-secondary" onclick="loadEntity()">OK</button>
			</div>
		</div>
		<div class="input-group mb-3">
			<div class="input-group-prepend">
				<div class="input-group-text">
					<input type="checkbox" id="get-stock"
						aria-label="Checkbox for following text input">
				</div>
			</div>
			<span class="input-group-text">Include Stock</span>
		</div>

		<!-- PAGINATION -->
		<nav>
			<ul class="pagination" id="navigation-panel"></ul>
		</nav>
		<div id="catalog-panel" class="row"></div>
	</div>
</div>
<script type="text/javascript">
	var page = 0;
	var limit = 8;
	var totalData = 0;
	var orderBy = null;
	var orderType = null;
	var defaultOption = "${defaultOption}";

	//elements
	var navigationPanel = document.getElementById("navigation-panel");
	var catalogPanel = document.getElementById("catalog-panel");
	var nameFilter = document.getElementById("search-name");
	var chkBoxGetStock = document.getElementById("get-stock");

	//detail
	var productTitle = document.getElementById("product-title");
	var productUnit = document.getElementById("product-unit");
	var productCategory = document.getElementById("product-category");
	var productDescription = document.getElementById("product-description");
	var carouselInner = document.getElementById("carousel-inner");
	var carouselIndicator = document.getElementById("carousel-indicators");
	var defaultLocation = window.location.href;

	function closeDetail() {
		hide("detail-content");
		show("catalog-content");
		window.history.pushState('catalog-page', 'Product Catalog',
				defaultLocation);
	}

	function populateDetail(entity) {
		console.log("entity", entity);
		hide("catalog-content");

		//POPULATE

		//title, count, price
		productTitle.innerHTML = entity.name;
		document.getElementById("product-stock").innerHTML = entity.count;
		document.getElementById("product-price").innerHTML = entity.price;
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
		var slash = "";
		if(!window.location.href.endsWith("/"))
			slash ="/";
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
					"withStock" : true
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
					"card-title", entity.name);
			cardTitle.onclick = function() {
				loadDetail(entity.code);
			}
			/* 	<h5 class="card-title">Product Name</h5> */
			//list group
			let listGroup = createElement("ul", "list-group-" + entity.id,
					"list-group");
			/* <ul class="list-group"> */
			//item list #1
			let listItemCount = createElement("li", "list-item-count-"
					+ entity.id,
					"list-group-item d-flex justify-content-between align-items-center");
			listItemCount.innerHTML = "Stock<br> <span class=\"badge badge-primary badge-pill\" "+
			" id=\"product-count-"+entity.id+"\">"
					+ entity.count + "</span>";
			/* <li
							class="list-group-item d-flex justify-content-between align-items-center">
					Stock<span class="badge badge-primary badge-pill"
							id="product-count">0</span>
				</li> */
			//item list #2
			let listItemPrice = createElement("li", "list-item-price-"
					+ entity.id,
					"list-group-item d-flex justify-content-between align-items-center");
			listItemPrice.innerHTML = "Price<br> <span id=\"product-price-"+entity.id+"\">"
					+ entity.price + "</span>";
			listGroup.append(listItemCount);
			listGroup.append(listItemPrice);

			//populate cardbody
			cardBody.append(cardTitle);
			cardBody.append(listGroup);

			//populate overall card
			cardDiv.append(iconImage);
			cardDiv.append(cardBody);

			//populate column
			colDiv.append(cardDiv);
			catalogPanel.append(colDiv);
		}
	}

	function loadEntity(page) {
		if (page < 0) {
			page = this.page;
		}
		var requestObject = {
			"entity" : "product",
			"filter" : {
				"limit" : limit,
				"page" : page,
				"orderBy" : orderBy,
				"orderType" : orderType,
				"fieldsFilter" : {
					"name" : nameFilter.value,
					"withStock" : chkBoxGetStock.checked
				}
			}
		};

		/* for (let i = 0; i < filterFields.length; i++) {
			let filterField = filterFields[i];
			if (filterField.value != "") {
				let fieldName = filterField.getAttribute("field");
				
			}
		} */
		doLoadEntities("<spring:url value="/api/public/get" />", requestObject,
				function(response) {
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
		defaultLocation = defaultLocation.replace("/"+defaultOption, "");
	}

	loadEntity(page);
</script>
