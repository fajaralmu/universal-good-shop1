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
	<jsp:include page="../catalog-component/product-filter.jsp"></jsp:include>
	<p></p>

	<!-- Detail Product -->
	<jsp:include page="../catalog-component/product-detail.jsp"></jsp:include>

	<!-- Catalog of Products -->

	<div id="catalog-content">
		<h2>Product Catalog</h2>
		<p></p>
		<div style="width: 100%">
			<!-- Navigations -->
			<nav>
				<ul class="pagination" id="navigation-panel"></ul>
			</nav>
			<div style= "padding: 10px">
				<!-- Display Options -->
				<button id="set-display-list" display="list" class="btn btn-outline-secondary b-select-display">
					<i  class="fa fa-list" aria-hidden="true"></i>
				</button>
				<button id="set-display-card" display="card" class="btn btn-secondary b-select-display">
					<i  class="fa fa-th" aria-hidden="true"></i>
				</button>
			</div>
			<div id="catalog-panel" style="grid-row-gap: 10px" class="row"></div>
		</div>
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
	var selectOrder = byId("select-order");

	//elements
	const navigationPanel = byId("navigation-panel");
	const catalogPanel = byId("catalog-panel");
	const nameFilter = byId("search-name");
	const chkBoxGetStock = byId("get-stock");
	const categoryFilter = byId("select-category");
	const tableSupplierList = byId("table-supplier-list");

	//detail
	const productTitle = byId("product-title");
	const productUnit = byId("product-unit");
	const productCategory = byId("product-category");
	const productDescription = byId("product-description");
	const carouselInner = byId("carousel-inner");
	const carouselIndicator = byId("carousel-indicators");
	var defaultLocation = window.location.href;
	var supplierOffset = 0;
	var selectedProductId = 0;
	var display = "card"; //OR list

	var URL_GET_PRODUCT_PUBLIC = "<spring:url value="/api/public/get" />";
	var URL_GET_SUPPLIER = "<spring:url value="/api/public/moresupplier" />";
	var IMAGE_PATH = "${host}/${contextPath}/${imagePath}/";

	var products = [];

	function setProducts(products) {
		this.products = products;
	}

	function updateCatalog() {
		populateCatalog(products);
	}

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
		const cardDisplay = this.display == "card";
		
		if(!cardDisplay){
			catalogPanel.appendChild(generateProductCatalogListHeaders());
		}
		
		for (let i = 0; i < products.length; i++) {
			const product = products[i];
			const productElement = cardDisplay ? createProductDisplayCard(product)
					: createProductDisplayList(product);

			//populate column
			catalogPanel.append(productElement);
		}
	}

	function updateNavigationButtons() {
		createNavigationButtons(navigationPanel, page, totalData, limit,
				loadEntity);
	}

	if (defaultOption != "") {
		loadDetail(defaultOption);
		defaultLocation = defaultLocation.replace(defaultOption, "");
		defaultLocation = defaultLocation.replace("/" + defaultOption, "");
	}

	function initEvents() {
		console.debug("initEvents");
		const displayOptions = document.getElementsByClassName("b-select-display");
		
		for (var i = 0; i < displayOptions.length; i++) {
			
			const btn = displayOptions[i];
			const display = btn.getAttribute("display");
			
			btn.onclick = function(e) {
				 
				setDisplay(display);
			}
		}

	}
	
	function resetDisplayOptionButtons(){
		const originalClass = "btn btn-outline-secondary b-select-display";
		const displayOptions = document.getElementsByClassName("b-select-display");
		
		for (var i = 0; i < displayOptions.length; i++) {
			const display = displayOptions[i].getAttribute("display");
			displayOptions[i].className = originalClass;
		}
	}

	function setDisplay(d) {
		console.info("setDisplay: ", d);
		try{
			resetDisplayOptionButtons();
			byId("set-display-"+d).className = "btn btn-secondary b-select-display";
		}catch (e) { }
		
		display = d;
		if (display == "card") {
			catalogPanel.className = "row";
		} else {
			catalogPanel.className = "table";
		}
		updateCatalog();
	}

	loadEntity(page);
	initEvents();
</script>
<c:if test="${authenticated == true }">
	<script type="text/javascript">
		function createListItemDetailLink(entity) {
			const id = "list-item-detail-link-" + entity.id;
			const className = "list-group-item d-flex justify-content-between align-items-center";
			const html = createHtmlTag({
				tagName : 'li',
				style : {
					'list-style' : 'none'
				},
				ch1 : {
					tagName : 'a',
					className : 'badge badge-secondary',
					href : "<spring:url value="/admin/product/" />"
							+ entity.code,
					innerHTML : 'setting'
				}
			});
			return html;
		}
		function addAdditionalLink(entity, listGroup) {
			const listItemDetailLink = createListItemDetailLink(entity);
			listGroup.append(listItemDetailLink);
		}
	</script>
</c:if>
