<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Shop::Purchase</title>
<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/css/bootstrap.css?version=1"></c:url> />
<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/css/shop.css?version=1"></c:url> />
<script src="<c:url value="/res/js/bootstrap.js"></c:url >"></script>
<script src="<c:url value="/res/js/ajax.js"></c:url >"></script>
<script src="<c:url value="/res/js/util.js"></c:url >"></script>
</head>
<body>
	<div class="container">
		<jsp:include page="../include/head.jsp"></jsp:include>
		<div class="content">
			<h2>Purchasing</h2>

			<table style="layout: fixed" class="table">
				<tr>
					<td>
						<div class="form">
							<p>ProductName</p>
							<input id="input-product" type="text" onkeyup="loadPrductList()"
								class="form-control" /> <br /> <select style="width: 300px"
								id="product-dropdown" class="form-control" multiple="multiple">
							</select>
							<hr>
							<p>Product Detail</p>
							<div class="panel">
								<p>
									Unit :<span id="unit-name"></span>
								</p>
								<p>Stock ID</p>
								<input disabled="disabled" type="number" class="form-control" id="stock-id"
									required="required" />
								<p>Stock</p>
								<input disabled="disabled" type="number" class="form-control" id="product-quantity"
									required="required" />
								<p>Price @Unit</p>
								<input disabled="disabled" type="number" class="form-control" id="product-price"
									required="required" />
								<p>Expiry Date</p>
								<input disabled="disabled" type="date" class="form-control" id="product-exp-date" />
								<p></p>
								<button id="add-product" onclick="addToChart()">Submit</button>
							</div>
						</div>
					</td>
					<td>
						<div class="form">
							<p>Customer Name</p>
							<input id="input-customer" type="text"
								onkeyup="loadCustomerList()" class="form-control" /> <br /> <select
								style="width: 200px" id="customer-dropdown" class="form-control"
								multiple="multiple">
							</select>
							<hr>
							<p>Customer Detail</p>
							<div class="panel">
								<h3 id="customer-name"></h3>
								<p id="customer-address"></p>
								<p id="customer-contact"></p>
							</div>
						</div>
					</td>
				</tr>
				<tr>
				</tr>
			</table>
			<div>
				<button id="btn-send" onclick="send()">Transaction</button>
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
				</thead>
				<tbody id="product-flows">

				</tbody>
			</table>
		</div>

		<jsp:include page="../include/foot.jsp"></jsp:include>
	</div>
	<script type="text/javascript">
		var productFlows = new Array();
		var currentProductFlow;
		var currentProduct;
		var currentCustomer;
		var inputProductField = document.getElementById("input-product");
		var stockIdField = document.getElementById("stock-id");
		var productListDropDown = document.getElementById("product-dropdown");
		var productFlowTable = document.getElementById("product-flows");

		var inputCustomerField = document.getElementById("input-customer");
		var customerListDropDown = document.getElementById("customer-dropdown");
		function send() {
			var requestObject = {
				"customer" : currentCustomer,
				"productFlows" : productFlows
			}
			postReq("<spring:url value="/api/transaction/supply" />",
					requestObject, function(xhr) {
						var response = (xhr.data);
						var code = response.code;
						if (code == "00") {
							alert("transaction success")

						} else {
							alert("transaction failed");
						}
					});
		}

		function loadCustomerList() {
			customerListDropDown.innerHTML = "";
			var requestObject = {
				"entity" : "customer",
				"filter" : {
					"page" : 0,
					"limit" : 10
				}
			};
			requestObject.filter.fieldsFilter = {};
			requestObject.filter.fieldsFilter["name"] = inputCustomerField.value;

			loadEntityList("<spring:url value="/api/entity/get" />",
					requestObject,
					function(entities) {
						for (let i = 0; i < entities.length; i++) {
							let entity = entities[i];
							let option = document.createElement("option");
							option.value = entity["id"];
							option.innerHTML = entity["name"];
							option.onclick = function() {
								inputCustomerField.value = option.innerHTML;
								document.getElementById("customer-name").innerHTML = entity.name;
								/* document.getElementById("customer-address").innerHTML = entity.address;
								document.getElementById("customer-contact").innerHTML = entity.contact; */
								currentCustomer = entity;
							}
							customerListDropDown.append(option);
						}
					});
		}

		function loadPrductList() {
			productListDropDown.innerHTML = "";
			var requestObject = {
				"product":{
					"name":inputProductField.value
				}
			};
			 
			loadEntityList("<spring:url value="/api/transaction/stocks" />",requestObject, function(entities) {
				for (let i = 0; i < entities.length; i++) {
					let flowEntity = entities[i];
					let entity = flowEntity.product;
					let option = document.createElement("option");
					option.value = entity["id"];
					option.innerHTML = flowEntity.id+ "-"+entity["name"] ;
					option.onclick = function() {
						setCurrentProduct(flowEntity);
					}
					productListDropDown.append(option);
				}
			});
		}

		
	</script>
	<script type="text/javascript">
		var priceField = document.getElementById("product-price");
		var quantityField = document.getElementById("product-quantity");
		var expiryDateField = document.getElementById("product-exp-date");

		function addToChart() {
			if (currentProduct == null) {
				alert("Product is not specified!");
				return;
			}

			let ID = Math.floor(Math.random() * 1000);
			if (currentProductFlow != null && currentProductFlow.id != null) {
				ID = currentProductFlow.id;
				removeFromProductFlowsById(ID);
			}
			let productFlow = {
				"id" : ID,
				"product" : currentProduct,
				"price" : priceField.value,
				"count" : quantityField.value,
				"expiryDate" : expiryDateField.value

			};

			productFlows.push(productFlow);
			populateProductFlow(productFlows);
			console.log("Product Flows", productFlows);
			currentProduct = null;
			currentProductFlow = null;
			clearProduct();
		}

		function clearProduct() {
			inputProductField.value = "";
			document.getElementById("unit-name").innerHTML = "";
			document.getElementById("product-dropdown").innerHTML = "";
			priceField.value = "";
			quantityField.value = "";
			expiryDateField.value = "";
		}

		function setCurrentProduct(entity) {
			inputProductField.value = entity.product.name;
			document.getElementById("unit-name").innerHTML = entity.product.unit.name;
			currentProduct = entity.product;
			priceField.value = entity.product.price;
			
			expiryDateField.value = entity.expiryDate;
			stockIdField.value = entity.id;
			
			
			//get remaining
			var requestObject = {
					"productFlow":{
						"id":entity.id
					}
			}
			
			postReq("<spring:url value="/api/transaction/stockinfo" />",
					requestObject, function(xhr) {
						var response = (xhr.data);
						var code = response.code;
						if (code == "00") {
							quantityField.value = response.productFlowStock.remainingStock;
						} else {
							alert("server error");
						}
					});

		}

		function removeFromProductFlowsById(ID) {
			productFlowTable.innerHTML = "";
			for (let i = 0; i < productFlows.length; i++) {
				let productFlow = productFlows[i];
				if (productFlow.id == ID)
					productFlows.splice(i, 1);
			}
		}

		function populateProductFlow(productFlows) {
			productFlowTable.innerHTML = "";
			for (let i = 0; i < productFlows.length; i++) {
				let productFlow = productFlows[i];
				let row = document.createElement("tr");
				row.append(createCell((i * 1 + 1) + ""));
				row.append(createCell(productFlow.id));
				row.append(createCell(productFlow.product.name));
				row.append(createCell(productFlow.expiryDate));
				row.append(createCell(productFlow.count));
				row.append(createCell(productFlow.price));

				let optionCell = createCell("");
				let btnEdit = createButton("edit-" + productFlow.id, "edit");

				let btnDelete = createButton("delete-" + productFlow.id,
						"delete");
				btnEdit.onclick = function() {
					setCurrentProductFlow(productFlow);
				}
				btnDelete.onclick = function() {
					if (!confirm("Are you sure wnat to delete?")) {
						return;
					}
					productFlows.splice(i, 1);
					populateProductFlow(productFlows);
				};
				optionCell.append(btnEdit);
				optionCell.append(btnDelete);
				row.append(optionCell);
				productFlowTable.append(row);
			}

		}

		function setCurrentProductFlow(entity) {
			currentProductFlow = entity;
			setCurrentProduct(entity.product);
			priceField.value = entity.price;
			quantityField.value = entity.count;
			expiryDateField.value = entity.expiryDate;
		}
	</script>
</body>
</html>