<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>

<div class="content">
	
	<div id="content-receipt" style="display: none">
		<h2>Receipt</h2>
		
		<table id="table-receipt" style="layout: fixed" class="table">
			
		</table>	
		<button id="btn-close-receipt" class="btn btn-secondary" onclick="hide('content-receipt'); show('content-form')"
		 >Ok</button>
		 <button id="btn-print-receipt" class="btn btn-secondary" >Print</button>
	</div>
	
	<div id="content-form">
		<h2>Add New Stock</h2>
		<table style="layout: fixed" class="table">
			<tr>
				<td>
					<div class="form">
						<p>ProductName</p>
						<input id="input-product" type="text" onkeyup="loadPrductList()"
							class="form-control" /> <br /> <select style="width: 200px"
							id="product-dropdown" class="form-control" multiple="multiple">
						</select>
						<hr>
						<p>Product Detail</p>
						<div class="panel">
							<p>
								Unit :<span id="unit-name"></span>
							</p>
							<p>
								Current Price :<span id="current-price"></span>
							</p>
							<p>Qty</p>
							<input type="number" class="form-control" id="product-quantity"
								required="required" />
							<p>Price @Unit</p>
							<input  class="form-control" id="product-price"
								required="required" />
							<p>Expiry Date</p>
							<input type="date" class="form-control" id="product-exp-date" />
							<p></p>
							<button class="btn btn-default" id="add-product"
								onclick="addToChart()">Add</button>
						</div>
					</div>
				</td>
				<td>
					<div class="form">
						<p>Supplier Name</p>
						<input id="input-supplier" type="text" onkeyup="loadSupplierList()"
							class="form-control" /> <br /> <select style="width: 200px"
							id="supplier-dropdown" class="form-control" multiple="multiple">
						</select>
						<hr>
						<p>Supplier Detail</p>
						<div class="panel">
							<h3 id="supplier-name"></h3>
							<p id="supplier-address"></p>
							<p id="supplier-contact"></p>
						</div>
					</div>
				</td>
			</tr>
			<tr>
			</tr>
		</table>
		<div>
			<button class="btn btn-primary" id="btn-send" onclick="send()">Submit-Transaction</button>
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
			<tbody id="product-flows">
	
			</tbody>
		</table>
	</div>
</div>
<script type="text/javascript">
	ENTITY_GET_URL = "<spring:url value="/api/entity/get" />";

	var productFlows = new Array();
	var currentProductFlow;
	var currentProduct;
	var currentSupplier;
	var inputProductField = _byId("input-product");
	var totalPriceLabel = _byId("total-price");
	var productListDropDown = _byId("product-dropdown");
	var productFlowTable = _byId("product-flows");
	var tableReceipt = _byId("table-receipt");

	var inputSupplierField = _byId("input-supplier");
	var supplierListDropDown = _byId("supplier-dropdown");
	function send() {
		if (!confirm("Are You Ready To Submit Transaction?"))
			return;

		if(!currentSupplier){
			alert("Supplier is not defined!");
			return;
		}
		
		var requestObject = {
			"supplier" : currentSupplier,
			"productFlows" : productFlows
		}
		postReq("<spring:url value="/api/transaction/supply" />",
				requestObject, function(xhr) {
					var response = (xhr.data);
					var code = response.code;
					if (code == "00") {
						alert("transaction success")
						productFlows = [];
						populateProductFlow(productFlows);
						showReceipt(response.transaction);
					} else {
						alert("transaction failed");
					}
				});
	}

	function showReceipt(transaction){
		const tableColumns = [
			["Code", transaction.code,""],
			["Date", new Date(transaction.transactionDate),""],
			["Type", transaction.type,""],
			["Supplier", transaction.supplier.name,""]
		];
		
		const tbody  = createTBodyWithGivenValue(tableColumns);
		tableReceipt.innerHTML = tbody.innerHTML; 
		
		var requestDetailFlows = {
			    "entity": "productFlow",
			    "filter": {
			        "limit": 0, 
			        "contains": false,
			        "exacts": true, 
			        "fieldsFilter": {
			            "transaction":transaction.code
			        }
			    }
			}; 
		
		doGetDetail("<spring:url value="/api/entity/get" />",requestDetailFlows, populateReceiptProductDetail);
		
		show("content-receipt");
		hide("content-form");
	} 
	
	function loadSupplierList() {
		 
		const filterValue = inputSupplierField.value; 
		
		loadStakeHolderList(supplierListDropDown, 'supplier', 'name', filterValue, 
				function(entity) {
					inputSupplierField.value = entity.name;
					_byId("supplier-name").innerHTML = entity.name;
					_byId("supplier-address").innerHTML = entity.address;
					_byId("supplier-contact").innerHTML = entity.contact;
					currentSupplier = entity;
		});
	}

	function loadPrductList() {
		productListDropDown.innerHTML = "";
		var requestObject = {
			"entity" : "product",
			"filter" : {
				"page" : 0,
				"limit" : 10,
				"fieldsFilter": {
					"name": inputProductField.value
				}
			}
		}; 

		loadEntityList("<spring:url value="/api/entity/get" />", requestObject,
				function(entities) {
					for (let i = 0; i < entities.length; i++) {
						const entity = entities[i];
						const option = createHtmlTag({
							tagName: 'option',
							id:  entity["id"],
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

	const priceField = _byId("product-price");
	const quantityField = _byId("product-quantity");
	const expiryDateField = _byId("product-exp-date");

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
		const productFlow = {
			"id" : ID,
			"product" : currentProduct,
			"price" : currentProduct.price,
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
		clearElement(inputProductField, priceField, quantityField, expiryDateField);
		clearElement("unit-name", "product-dropdown", "current-price"); 
	}

	function setCurrentProduct(entity) {
		inputProductField.value = entity.name;
		_byId("unit-name").innerHTML = entity.unit.name;
		_byId("current-price").innerHTML = beautifyNominal(entity.price);
		currentProduct = entity;

	}
 
	function populateProductFlow(productFlows) {
		doPopulateProductFlow(productFlows, function(i, productFlow, row){
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
</script>
<c:if test="${requestCode != null }">
	<script type="text/javascript">
		var requestTransactionCode = "${requestCode}";
		const requestObject = {
			    "entity": "transaction",
			    "filter": {
			        "limit": 1,
			        "orderBy": null,
			        "orderType": null,
			        "exacts":true,
			        "contains":false,
			        "fieldsFilter": {
			            "code": requestTransactionCode,
			            "type":"IN"
			        }
			    }
			};
		doGetById("<spring:url value="/api/entity/get" />", requestObject, function(entity){
			showReceipt(entity);
		});	
		
	</script>
</c:if>