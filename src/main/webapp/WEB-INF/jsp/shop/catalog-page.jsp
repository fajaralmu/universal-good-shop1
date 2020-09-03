<%@page import="com.fajar.shoppingmart.entity.User"%>
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
				<td style="width: 14%">
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
	const navigationPanel = _byId("navigation-panel");
	const catalogPanel = _byId("catalog-panel");
	const nameFilter = _byId("search-name");
	const chkBoxGetStock = _byId("get-stock");
	const categoryFilter = _byId("select-category");
	const tableSupplierList = _byId("table-supplier-list");

	//detail
	const productTitle = _byId("product-title");
	const productUnit = _byId("product-unit");
	const productCategory = _byId("product-category");
	const productDescription = _byId("product-description");
	const carouselInner = _byId("carousel-inner");
	const carouselIndicator = _byId("carousel-indicators");
	var defaultLocation = window.location.href;
	var supplierOffset = 0;
	var selectedProductId = 0;
	
	var URL_GET_PRODUCT_PUBLIC = "<spring:url value="/api/public/get" />";
	var URL_GET_SUPPLIER = "<spring:url value="/api/public/moresupplier" />";
	var IMAGE_PATH = "${host}/${contextPath}/${imagePath}/";

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
 
	function createListGroup(entity){
		 return createHtmlTag({
				tagName: "ul",
				id: "list-group-" + entity.id,
				className: "list-group",
				style: {color: '#000000'}
			});
	}
	
	function createListItemCount(entity){
		const listClass = "list-group-item d-flex justify-content-between align-items-center";
		const listItem = createHtmlTag({
			tagName: "li", 
			id: "list-item-count-" + entity.id,
			className: listClass,
			ch1: { tagName:"span", innerHTML : "Stock" },
			ch2: {tagName: "br"},
			ch3: { 
				tagName: "span",
				className: "badge badge-primary badge-pill",
				innerHTML: entity.count,
				id: "product-count-"+entity.id
			} 
		});
		 
		return listItem;
	}
	
	function createProductCardTitle(entity){
		const html = createHtmlTag({ 
			tagName: 'h5', 
			id: 'title-'+entity.id,
			ch1: {
				tagName:'small', 
				style: { 'background-color':'rgb(224,224,224)'},
				className: 'text-muted clickable',
				innerHTML: (entity.newProduct?"(NEW)":entity.name)
			}, 
			onclick: function() {
				loadDetail(entity.code);
			}
		}); 
		/* html.onclick = function() {
			loadDetail(entity.code);
		} */
		return html;
	}
	
	function createCategoryTag(entity){
		const html = createHtmlTag({
			tagName: 'h5',
			id: 'category-'+entity.id,
			ch1:{
				tagName: 'span',
				className: 'badge badge-secondary',
				innerHTML: entity.category.name
			}
		}); 
		return html;
	}
	
	function createProductIconElement(entity){
		const imageUrl = entity.imageUrl; 
		const src = "${host}/${contextPath}/${imagePath}/"
				+ imageUrl.split("~")[0]; 
		const elementId = "icon-" + entity.id;
		
		const iconImage = createImgTag(elementId, "card-img-top", "100", "150", src);
		iconImage.setAttribute("alt", entity.name);
		
		return iconImage;
	}
	
	function createListItemPrice(entity){
		const id = "list-item-price-" + entity.id;
		const className = "list-group-item d-flex justify-content-between align-items-center";
		const html = createHtmlTag({
			tagName: 'li',
			id: id, 
			className: className,
			ch1: {tagName: 'span', innerHTML: 'Price'},
			ch2: {tagName: 'br'},
			ch3: {
				tagName: 'span', 
				className: 'text-warning', 
				id: "product-price-"+entity.id,
				innerHTML: beautifyNominal(entity.price)
				}
			
		}); 
		return html;
	}
	
	function createListItemDetailLink(entity){
		const id = "list-item-detail-link-" + entity.id;
		const className = "list-group-item d-flex justify-content-between align-items-center";
		const html = createHtmlTag({
			tagName: 'li',
			style: {'list-style':'none'},
			ch1: {
				tagName: 'a',
				style: {'background-color':'gray', 'color': 'white'},
				href: "<spring:url value="/admin/product/" />"+entity.code,
				innerHTML: 'setting'
			} 
		});
		return html;
	}
	
	/* function createProductDetailLink(entity){
		const id = "product-info-link-" + entity.id;
		const className = "list-group-item d-flex justify-content-between align-items-center";
		const html = createHtmlTag({
			tagName: 'li',
			style: {'list-style':'none'},
			ch1: {
				tagName: 'a',
				style: {'background-color':'gray', 'color': 'white'},
				href: "<spring:url value="/public/catalog/" />"+entity.code,
				ch1: {tagName: 'h5', innerHTML: entity.name }
			} 
		});
		return html;
	} */
	
	function populateCatalog(products) {
		catalogPanel.innerHTML = "";
		for (let i = 0; i < products.length; i++) {
			const entity = products[i];

			//create col
			const colDiv = createDiv("col-" + entity.id, "col-sm-4");
			//create card
			const cardDiv = createHtmlTag({
				tagName: "div",
				id : "card-" + entity.id,
				className: "card",
				style: {'width': '100%', 'background-color': entity.color, 'color': entity.color}
			});  
			 
			const iconImage = createProductIconElement(entity); 
			const categoryTag = createCategoryTag(entity); 
			const cardBody = createDiv("card-body-" + entity.id, "card-body");
			/* <div class="card-body"> */
			//card  title
			const cardTitle = createProductCardTitle(entity); 

			///LIST GROUP///
			const listGroup = createListGroup(entity);
			 
			//////////LIST ITEMS//////////
			
			//const productDetailLink = createProductDetailLink(entity);
			const listItemCount = createListItemCount(entity);  
			const listItemPrice = createListItemPrice(entity);
			
			//listGroup.append(productDetailLink);
			listGroup.append(listItemCount);
			listGroup.append(listItemPrice);
			
			if(this.addAdditionalLink){
				this.addAdditionalLink(entity, listGroup);
			} 
			
			
			//populate cardbody
			cardBody.append(cardTitle);
			cardBody.append(listGroup); 
			cardBody.append(categoryTag);
			//populate overall card
			cardDiv.append(iconImage);
			cardDiv.append(cardBody);

			//populate column
			colDiv.append(cardDiv);
			catalogPanel.append(colDiv);
		}
	}
 
	function updateNavigationButtons() {
		createNavigationButtons( navigationPanel, page,
				 totalData,  limit, loadEntity);
	}

	if (defaultOption != "") {
		loadDetail(defaultOption);
		defaultLocation = defaultLocation.replace(defaultOption, "");
		defaultLocation = defaultLocation.replace("/" + defaultOption, "");
	}

	loadEntity(page);
</script>
<c:if test="${authenticated == true }">
<script type="text/javascript">
	function addAdditionalLink(entity, listGroup){
		const listItemDetailLink = createListItemDetailLink(entity);
		listGroup.append(listItemDetailLink);
	}
</script>
</c:if>
