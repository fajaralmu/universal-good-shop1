<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">

	<jsp:include page="../transaction-component/receipt.jsp"></jsp:include>

	<div id="content-form">
		<h2>Selling</h2>
		<div class="row">
			<div class="col-6">
				<div class="form">
					<div class="card">
						<div class="card-header"><i class="fa fa-cart-arrow-down" aria-hidden="true"></i> Product</div>
						<div class="card-body">
							<form onsubmit="event.preventDefault(); loadProductList('code', 'input-product-code', true)">
								<div class="dynamic-dropdown-form">
									<input id="input-product" placeholder="product name"
										type="text" onkeyup="loadProductList('name', 'input-product', false)" class="form-control" />
									<input id="input-product-code" placeholder="product code"
										type="text" class="form-control onenter" />
									<select id="product-dropdown" class="form-control" multiple="multiple"></select>
								</div>
								<input type="submit" style="display: none"/>
							</form>
						</div>
					</div>
					<div class="panel trans-form">
						<p>Unit</p>
						<span id="unit-name"></span>
						<p>Stock</p>
						<input disabled="disabled" type="number" class="form-control"
							id="stock-quantity" required="required" />
						<p>Product Quantity</p>
						<input type="number" class="form-control" id="product-quantity"
							required="required" />
						<p>Price @Unit</p>
						<input disabled="disabled" class="form-control"
							id="product-price" required="required" />
						<p hidden="true">Expiry Date</p>
						<input hidden="true" disabled="disabled" type="date"
							class="form-control" id="product-exp-date" />

						<button class="btn btn-outline-info btn-sm" id="add-product"
							onclick="addToCart()"><i class="fa fa-cart-plus" aria-hidden="true"></i></button>
						<button class="btn btn-primary btn-sm" id="btn-send"
							onclick="send()">Submit Transaction</button>
					</div>
				</div>
			</div>
			<div class="col-6">
				<jsp:include page="../transaction-component/customer-form.jsp"></jsp:include>
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
					<th>Reff Stock ID</th>
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
		<div class="trans-form" style="width: 50%">
			<p>Total Price</p>
			<input type="number" id="total-price-label" disabled="disabled"
				class="form-control" />
			<p>Purchase Price</p>
			<input type="number" required="required" id="purchase-price"
				onkeyup="calculateChange()" class="form-control" />
			<p>Change</p>
			<input type="number" id="total-change-label" disabled="disabled"
				class="form-control" />
		</div>
	</div>
</div>

<script type="text/javascript">
	//has been declared;
	ENTITY_GET_URL = "<spring:url value="/api/entity/get" />";

	var productFlows = new Array();
	var currentProductFlow;
	var currentProduct;
	var currentCustomer;

	const inputProductName = byId("input-product");
	const inputProductCode = byId("input-product-code");
	//const stockIdField = byId("stock-id");
	const totalPriceLabel = byId("total-price");
	const productListDropDown = byId("product-dropdown");
	const productFlowTable = byId("product-flows");
	const tableReceipt = byId("table-receipt"); 
	
	const inputTransactionMode = byId("select-transaction-mode");
	
	function send() {
		confirmDialog("Are You Ready To Submit "+inputTransactionMode.value+" Transaction?").then(function(ok){
			if(ok){
				submitTransaction();
			}
		});
	}
	
	function submitTransaction() {
		
		var requestObject = {
			"customer" : currentCustomer,
			"productFlows" : productFlows,
			"transaction": {"mode":inputTransactionMode.value}
		}
		postReq("<spring:url value="/api/transaction/selling" />",
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
				[ "Customer", transaction.customer.name, "" ] ];

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
			'entity' : 'product',
			'filter' : {
				'exacts': exacts,
				'fieldsFilter' :  fieldsFilter
			}
		};

		loadEntityList(ENTITY_GET_URL, requestObject, function(entities) {
			for (let i = 0; i < entities.length; i++) {

				const product = entities[i];
				const option = createHtmlTag({
					tagName : 'option',
					value : product['id'],
					innerHTML : product['name'],
					onclick : function() {
						getProductFlow(product, true);
					}
				})

				productListDropDown.append(option);
			}
		});
	}

	/***COMPONENT OPERATION***/

	const priceField = byId("product-price");
	const quantityField = byId("stock-quantity");
	const inputQuantityField = byId("product-quantity");
	const expiryDateField = byId("product-exp-date");
	const unitNameLabel = byId("unit-name");

	function addToCart() {
		if (currentProduct == null) {
			alert("Product is not specified!");
			return;
		}

		if (!isEditMode() && getCurrentProductFlow(currentProduct.code)) {
			alert("product is exist in the cart!");
			return;
		}

		const inputQty = parseInt(inputQuantityField.value);

		if (!inputQty || inputQty > quantityField.value * 1) {
			alert("Quantity insufficient");
			return;
		}
		var ID = randomID();
		if (currentProductFlow != null && currentProductFlow.id != null) {
			ID = currentProductFlow.id;
			removeFromProductFlowsById(ID);
		}
		const productFlow = {
			"id" : ID,
			"product" : currentProduct,
			"price" : isReturnMode() ? priceField.value : currentProduct.price,
			"count" : inputQuantityField.value,
			"expiryDate" : expiryDateField.value,
		//"flowReferenceId":stockIdField.value

		};

		addProductFlow(productFlow);
		//console.log("Product Flows", productFlows);
		currentProduct = null;
		currentProductFlow = null;
		clearProductInputs();
		disableEditMode();
	}

	function addProductFlow(productFlow) {
		productFlows.push(productFlow);
		populateProductFlow(productFlows);
	}

	function calculateChange() {
		const totalPrice = byId("total-price-label").value;
		const puchaseValue = byId("purchase-price").value;
		byId("total-change-label").value = puchaseValue - totalPrice;
	}

	function clearProductInputs() {
		clearElement(inputProductName, inputProductCode, priceField, quantityField,
				inputQuantityField, expiryDateField);
		clearElement("unit-name", "product-dropdown", "total-change-label",
				"purchase-price");
		
		//quantityField.value = 0;
		//priceField.value = 0;
		//quantityField.value = 0; 
	}

	function setCurrentProduct(entity, loadNewStock) {
		inputProductName.value = entity.product.name;
		inputProductCode.value = entity.product.code;
		priceField.value = parseInt(entity.product.price);
		quantityField.value = entity.product.count;

		unitNameLabel.innerHTML = entity.product.unit.name;

		if (!entity.count) {
			inputQuantityField.value = 0;
		} else {
			inputQuantityField.value = entity.count;
		}

		currentProduct = entity.product;

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
			row.append(createCell(productFlow.flowReferenceId));

		});
	}

	function getProductFlow(product) {
		const requestObject = {
			"entity" : "product",
			"filter" : {
				'limit' : 20,
				"fieldsFilter" : {
					"code[EXACTS]" : product.code,
					"withStock" : true
				}
			}
		};
		doLoadEntities("<spring:url value="/api/public/get" />", requestObject,
				function(res) {
					if (!res || res.code != "00") {
						alert("Data Not Found");
						return;
					}
					const entities = res.entities;
					const productDetailed = entities[0];
					const productFlowObj = {
						product : productDetailed
					};
					setCurrentProductFlow(productFlowObj);
				});
	}

	function setCurrentProductFlow(entity) {
		currentProductFlow = entity;
		priceField.value = entity.price;

		//	quantityField.value = entity.productFlowStock.remainingStock;
		//	expiryDateField.value = entity.expiryDate;
		setCurrentProduct(entity, true);
	}
	
	function setRegularMode(){
		inputTransactionMode.value = "REGULAR";
		changeMode();
	}
	
	function changeMode(){
		clearProductInputs();
		populateProductFlow([]);
		
		if(isReturnMode()){
			priceField.removeAttribute("disabled");
		} else {
			priceField.setAttribute("disabled", "disabled");
		}
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
		const requestTransactionCode = "${requestCode}";
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
					"type" : "SELLING"
				}
			}
		};
		doGetById("<spring:url value="/api/entity/get" />", requestObject,
				function(entity) {
					showReceipt(entity);
				});
	</script>
</c:if>