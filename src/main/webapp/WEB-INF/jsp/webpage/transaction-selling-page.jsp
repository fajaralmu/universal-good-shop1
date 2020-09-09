<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">

	<jsp:include page="../transaction-component/receipt.jsp"></jsp:include>

	<div id="content-form">
		<h2>Selling</h2>
		<table style="layout: fixed" class="table">
			<tr>
				<td>
					<div class="form">
						<div class="card">
							<div class="card-header"><i class="fa fa-cart-arrow-down" aria-hidden="true"></i> Product</div>
							<div class="card-body">
								<div class="dynamic-dropdown-form">
									<input id="input-product" placeholder="product name"
										type="text" onkeyup="loadProductList()" class="form-control" />
									<select id="product-dropdown" class="form-control"
										multiple="multiple">
									</select>
								</div>
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
				</td>
				<td><jsp:include
						page="../transaction-component/customer-form.jsp"></jsp:include></td>
			</tr>
		</table>
		
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
			<tbody id="product-flows">

			</tbody>
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
	//const stockIdField = byId("stock-id");
	const totalPriceLabel = byId("total-price");
	const productListDropDown = byId("product-dropdown");
	const productFlowTable = byId("product-flows");
	const tableReceipt = byId("table-receipt");

	function send() {
		if (!confirm("Are You Ready To Submit Transaction?"))
			return;
		var requestObject = {
			"customer" : currentCustomer,
			"productFlows" : productFlows
		}
		postReq("<spring:url value="/api/transaction/selling" />",
				requestObject, function(xhr) {
					var response = (xhr.data);
					var code = response.code;
					if (code == "00") {
						alert("transaction success")
						productFlows = [];
						populateProductFlow(productFlows);
						showReceipt(response.transaction)
						clearProductInputs();
					} else {
						alert("transaction failed");
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

	function loadProductList() {
		productListDropDown.innerHTML = "";
		var requestObject = {
			'entity' : 'product',
			'filter' : {
				'fieldsFilter' : {
					"name" : inputProductName.value
				}
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

		if (getCurrentProductFlow(currentProduct.code)) {
			alert("product is exist in the cart!");
			return;
		}

		const inputQty = +inputQuantityField.value;

		if (!inputQty || inputQty > quantityField.value * 1) {
			alert("Quantity insufficient");
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
			"price" : currentProduct.price,
			"count" : inputQuantityField.value,
			"expiryDate" : expiryDateField.value,
		//"flowReferenceId":stockIdField.value

		};

		addProductFlow(productFlow);
		console.log("Product Flows", productFlows);
		currentProduct = null;
		currentProductFlow = null;
		clearProductInputs();
	}

	function getCurrentProductFlow(code) {
		if (productFlows) {
			for (var i = 0; i < productFlows.length; i++) {
				if (productFlows[i].product.code == code) {
					return productFlows[i];
				}
			}
		}
		return null;
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
		clearElement(inputProductName, priceField, quantityField,
				inputQuantityField, expiryDateField);
		clearElement("unit-name", "product-dropdown", "total-change-label",
				"purchase-price");

		//quantityField.value = 0;
		//priceField.value = 0;
		//quantityField.value = 0; 
	}

	function setCurrentProduct(entity, loadNewStock) {
		inputProductName.value = entity.product.name;
		priceField.value = beautifyNominal(entity.product.price);
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
					"type" : "OUT"
				}
			}
		};
		doGetById("<spring:url value="/api/entity/get" />", requestObject,
				function(entity) {
					showReceipt(entity);
				});
	</script>
</c:if>