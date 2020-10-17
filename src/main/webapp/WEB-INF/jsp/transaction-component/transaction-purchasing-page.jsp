<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">

	<jsp:include page="../transaction-component/receipt.jsp"></jsp:include>

	<div id="content-form">
		<h2>Purchasing</h2>
		<div class="row">
			<div class="col-6">
				<div class="form">
					<div class="card">
						<div class="card-header"><i class="fa fa-cart-arrow-down" aria-hidden="true"></i> Product</div>
						<div class="card-body">
							<div class="dynamic-dropdown-form">
								<input id="input-product" placeholder="product name"
									type="text" onkeyup="loadProductList('name', 'input-product', false)" class="form-control" />
								<input id="input-product-code" placeholder="product code"
									type="text" on-enter="loadProductList('code', 'input-product-code', true)" class="form-control onenter" />
								<select
									id="product-dropdown" class="form-control" multiple="multiple">
								</select>
							</div>
						</div>
					</div>
					<div class="panel trans-form">
						<p>Unit</p>
						<span id="unit-name"></span>
						<p>Current Price</p>
						<span id="current-price"></span>
						<p>Qty</p>
						<input type="number" class="form-control" id="product-quantity"
							required="required" />
						<p>Price @Unit</p>
						<input class="form-control" id="product-price"
							required="required" />
						<p>Expiry Date</p>
						<input type="date" class="form-control" id="product-exp-date" />

						<button class="btn btn-outline-info btn-sm" id="add-product"
							onclick="addToCart()"><i class="fa fa-cart-plus" aria-hidden="true"></i></button>
						<button class="btn btn-primary btn-sm" id="btn-send" onclick="send()">Submit-Transaction</button>
					</div>
				</div>
			</div>
			<div class="col-6">
				<jsp:include page="../transaction-component/supplier-form.jsp"></jsp:include>
			</div>
			<div class="col-6">
				<p>Transaction Mode</p>
				<select class="form-control" id="select-transaction-mode">
					<c:forEach items="${transactionModes }" var="mode">
						<option value="${mode.value }">${mode.value }</option>
					</c:forEach>
				</select>
			</div>
		</div>
		 
		<table class="table">
			<thead>
				<tr>
					<th>No</th>
					<th>Flow ID</th>
					<th>Product Name</th>
					<th>Expiry Date</th>
					<th>Quantity</th>
					<th>Price @Item</th>
					<th>Option</th>
				</tr>
				<tr>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th>Total:<span id="total-price"></span>
					</th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody id="product-flows"></tbody>
		</table>
	</div>
</div>
<script type="text/javascript">
	ENTITY_GET_URL = "<spring:url value="/api/entity/get" />";

	var productFlows = new Array();
	var currentProductFlow;
	var currentProduct;
	var currentSupplier;
	
	const inputProductField = byId("input-product");
	const inputProductCode = byId("input-product-code");
	
	const totalPriceLabel = byId("total-price");
	const productListDropDown = byId("product-dropdown");
	const productFlowTable = byId("product-flows");
	const tableReceipt = byId("table-receipt");
	
	const inputTransactionMode = byId("select-transaction-mode");

	function send(){
		confirmDialog("Are You Ready To Submit "+inputTransactionMode.value+" Transaction?").then(function(ok){
			if(ok){
				submitTransaction();
			}
		});
	}
	
	function submitTransaction() {

		if (!currentSupplier) {
			infoDialog("Supplier is not defined!").then(function(e){});
			return;
		}

		var requestObject = {
			"supplier" : currentSupplier,
			"productFlows" : productFlows,
			"transaction": {"mode":inputTransactionMode.value}
		}
		postReq("<spring:url value="/api/transaction/purchasing" />",
				requestObject, function(xhr) {
					const response = (xhr.data);
					const code = response.code;
					if (code == "00") {
						
						infoDialog("transaction success").then(function(e){
							setRegularMode();
							showReceipt(response.transaction);
						});
						
					} else {
						infoDialog("transaction failed").then(function(e){});
					}
				});
	}

	function showReceipt(transaction) {
		const tableColumns = [ [ "Code", transaction.code, "" ],
				[ "Date", new Date(transaction.transactionDate), "" ],
				[ "Type", transaction.type, "" ],
				[ "Supplier", transaction.supplier.name, "" ] ];

		const tbody = createTBodyWithGivenValue(tableColumns);
		tableReceipt.innerHTML = tbody.innerHTML;

		processReceipt(transaction);
	}

	function loadProductList(fieldName, inputId, exacts) {
		productListDropDown.innerHTML = "";
		const inputElement = byId(inputId);
		const fieldsFilter = {};
		fieldsFilter[fieldName] = inputElement.value;
		
		var requestObject = {
			"entity" : "product",
			"filter" : {
				"page" : 0,
				"limit" : 10,
				"fieldsFilter" :  fieldsFilter,
				'exacts' : exacts
			}
		};

		loadEntityList(ENTITY_GET_URL, requestObject, function(entities) {
			for (let i = 0; i < entities.length; i++) {
				const entity = entities[i];
				const option = createHtmlTag({
					tagName : 'option',
					id : entity["id"],
					innerHTML : entity['name'],
					onclick : function() {
						setCurrentProduct(entity);
					}
				});
				productListDropDown.append(option);
			}
		});
	}

	/***COMPONENT OPERATION***/

	const priceField = byId("product-price");
	const quantityField = byId("product-quantity");
	const expiryDateField = byId("product-exp-date");

	function addToCart() {
		if (currentProduct == null) {
			alert("Product is not specified!");
			return;
		}

		let ID = randomID();
		if (currentProductFlow != null && currentProductFlow.id != null) {
			ID = currentProductFlow.id;
			removeFromProductFlowsById(ID);
		}
		const productFlow = {
			"id" : ID,
			"product" : currentProduct,
			"price" : priceField.value,
			"count" : quantityField.value,
			"expiryDate" : expiryDateField.value

		};

		addProductFlow(productFlow);
		console.log("Product Flows", productFlows);
		currentProduct = null;
		currentProductFlow = null;
		clearProduct();
	}

	function addProductFlow(productFlow) {
		productFlows.push(productFlow);
		populateProductFlow(productFlows);
	}

	function clearProduct() {
		clearElement(inputProductField, inputProductCode, priceField, quantityField,
				expiryDateField);
		clearElement("unit-name", "product-dropdown", "current-price");
	}

	function setCurrentProduct(entity) {
		inputProductField.value = entity.name;
		inputProductCode.value = entity.code;
		byId("unit-name").innerHTML = entity.unit.name;
		byId("current-price").innerHTML = beautifyNominal(entity.price);
		currentProduct = entity;

	}

	function populateProductFlow(productFlows) {
		this.productFlows = productFlows;
		doPopulateProductFlow(productFlows, function(i, productFlow, row) {
			
			row.append(createCell((i * 1 + 1) + ""));
			row.append(createCell(productFlow.id));
			row.append(createCell(productFlow.product.name));
			row.append(createCell(productFlow.expiryDate));
			row.append(createCell(productFlow.count));
			row.append(createCell(beautifyNominal(productFlow.price)));
		});
	}

	function setCurrentProductFlow(entity) {
		currentProductFlow = entity;
		setCurrentProduct(entity.product);
		priceField.value = beautifyNominal(entity.price);
		quantityField.value = entity.count;
		expiryDateField.value = entity.expiryDate;
	}
	
	function setRegularMode(){
		inputTransactionMode.value = "REGULAR";
		changeMode();
	}
	
	function changeMode(){
		clearProduct();
		populateProductFlow([]);
	}
	
	function isReturnMode(){
		return inputTransactionMode.value == "RETURN";
	}
	
	inputTransactionMode.onchange = function(e){
		confirmDialog("Change Mode?").then(function(ok){
			if(ok){
				changeMode();
			}
		});
	}
</script>
<c:if test="${requestCode != null }">
	<script type="text/javascript">
		var requestTransactionCode = "${requestCode}";
		const requestObject = {
			"entity" : "transaction",
			"filter" : {
				"limit" : 1,
				"orderBy" : null,
				"orderType" : null,
				"exacts" : true,
				"contains" : false,
				"fieldsFilter" : {
					"code" : requestTransactionCode,
					"type" : "IN"
				}
			}
		};
		doGetById("<spring:url value="/api/entity/get" />", requestObject,
				function(entity) {
					showReceipt(entity);
				});
	</script>
</c:if>