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
	var productFlows = new Array();
	var currentProductFlow;
	var currentProduct;
	var currentSupplier;
	var inputProductField = document.getElementById("input-product");
	var totalPriceLabel = document.getElementById("total-price");
	var productListDropDown = document.getElementById("product-dropdown");
	var productFlowTable = document.getElementById("product-flows");
	var tableReceipt = document.getElementById("table-receipt");

	var inputSupplierField = document.getElementById("input-supplier");
	var supplierListDropDown = document.getElementById("supplier-dropdown");
	function send() {
		if (!confirm("Are You Ready To Submit Transaction?"))
			return;

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
		let tableColumns = [
			["Code", transaction.code,""],
			["Date", new Date(transaction.transactionDate),""],
			["Type", transaction.type,""],
			["Supplier", transaction.supplier.name,""]
		];
		
		let tbody  = createTBodyWithGivenValue(tableColumns);
		tableReceipt.innerHTML = "";
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
		var detailFields = ["NO","Product","ID","Expiry Date","Qty","Unit","Price","Total Price"];
		
		doGetDetail("<spring:url value="/api/entity/get" />",requestDetailFlows,detailFields, populateProductFlowDetail);
		
		show("content-receipt");
		hide("content-form");
	}
	
	function populateProductFlowDetail(entities,detailFields){
		var tableColumns = [];
		tableColumns.push(detailFields);
		var summaryPrice = 0;
		for (let i = 0; i < entities.length; i++) {
			let productFlow = entities[i];
			let totalPrice = productFlow.count*1 * productFlow.price*1;
			let columns = [
				i+1,
				productFlow.product.name, productFlow.id, productFlow.expiryDate, productFlow.count,
				productFlow.product.unit.name, productFlow.price, totalPrice
				];
			summaryPrice += totalPrice;
			tableColumns.push(columns);
		}
		let tbody  = createTBodyWithGivenValue(tableColumns);
		tableReceipt.innerHTML+="<tr><td>Transaction Amount</td><td style=\"text-align:left\" colspan=\"2\"><u>"+beautifyNominal(summaryPrice)+"</u></td></tr>";
		tableReceipt.innerHTML+="<tr><td style=\"text-align:center\" colspan=\"7\"><h3>Products</h3></td></tr>";
		tableReceipt.innerHTML+=tbody.innerHTML;
		tableReceipt.innerHTML+="<tr><td style=\"text-align:right\" colspan=\"7\">Total : "+beautifyNominal(summaryPrice)+"</td></tr>";
		
	}
	
	function loadSupplierList() {
		supplierListDropDown.innerHTML = "";
		var requestObject = {
			"entity" : "supplier",
			"filter" : {
				"page" : 0,
				"limit" : 10
			}
		};
		requestObject.filter.fieldsFilter = {};
		requestObject.filter.fieldsFilter["name"] = inputSupplierField.value;

		loadEntityList(
				"<spring:url value="/api/entity/get" />",
				requestObject,
				function(entities) {
					for (let i = 0; i < entities.length; i++) {
						let entity = entities[i];
						let option = document.createElement("option");
						option.value = entity["id"];
						option.innerHTML = entity["name"];
						option.onclick = function() {
							inputSupplierField.value = option.innerHTML;
							document.getElementById("supplier-name").innerHTML = entity.name;
							document.getElementById("supplier-address").innerHTML = entity.address;
							document.getElementById("supplier-contact").innerHTML = entity.contact;
							currentSupplier = entity;
						}
						supplierListDropDown.append(option);
					}
				});
	}

	function loadPrductList() {
		productListDropDown.innerHTML = "";
		var requestObject = {
			"entity" : "product",
			"filter" : {
				"page" : 0,
				"limit" : 10
			}
		};
		requestObject.filter.fieldsFilter = {};
		requestObject.filter.fieldsFilter["name"] = inputProductField.value;

		loadEntityList("<spring:url value="/api/entity/get" />", requestObject,
				function(entities) {
					for (let i = 0; i < entities.length; i++) {
						let entity = entities[i];
						let option = document.createElement("option");
						option.value = entity["id"];
						option.innerHTML = entity["name"];
						option.onclick = function() {
							setCurrentProduct(entity);
						}
						productListDropDown.append(option);
					}
				});
	}

	/***COMPONENT OPERATION***/

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
		inputProductField.value = "";
		document.getElementById("unit-name").innerHTML = "";
		document.getElementById("product-dropdown").innerHTML = "";
		priceField.value = "";
		quantityField.value = "";
		expiryDateField.value = "";
	}

	function setCurrentProduct(entity) {
		inputProductField.value = entity.name;
		document.getElementById("unit-name").innerHTML = entity.unit.name;
		currentProduct = entity;

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
		let totalPrice = 0;
		for (let i = 0; i < productFlows.length; i++) {
			let productFlow = productFlows[i];
			let row = document.createElement("tr");
			row.append(createCell((i * 1 + 1) + ""));
			row.append(createCell(productFlow.id));
			row.append(createCell(productFlow.product.name));
			row.append(createCell(productFlow.expiryDate));
			row.append(createCell(productFlow.count));
			row.append(createCell(beautifyNominal(productFlow.price)));

			let optionCell = createCell("");
			let btnEdit = createButton("edit-" + productFlow.id, "edit");

			let btnDelete = createButton("delete-" + productFlow.id, "delete");
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

			totalPrice = totalPrice * 1
					+ (productFlow.price * productFlow.count);
		}
		totalPriceLabel.innerHTML = beautifyNominal(totalPrice);
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
		var requestObject = {
			    "entity": "transaction",
			    "filter": {
			        "limit": 1,
			        "orderBy": null,
			        "orderType": null,
			        "exacts":true,
			        "contains":false,
			        "fieldsFilter": {
			            "code": requestTransactionCode,
			            "type":"OUT"
			        }
			    }
			};
		doGetById("<spring:url value="/api/entity/get" />", requestObject, function(entity){
			showReceipt(entity);
		});	
		
	</script>
</c:if>