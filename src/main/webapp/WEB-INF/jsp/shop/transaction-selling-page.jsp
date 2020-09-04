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
		<button id="btn-close-receipt" class="btn btn-secondary"
			onclick="hide('content-receipt'); show('content-form')">Ok</button>
		<button id="btn-print-receipt" class="btn btn-secondary">Print</button>
	</div>
	<div id="content-form">
		<h2>Purchasing</h2>
		<table style="layout: fixed" class="table">
			<tr>
				<td>
					<div class="form">
						<p>Stock ID</p>
						<input type="number" class="form-control" id="stock-id"
							required="required" />
						<button id="search-stock" class="btn btn-outline-secondary btn-sm" onclick="stockInfo()">OK</button>
						<p>Or Put ProductName</p>
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

							<p>Stock</p>
							<input disabled="disabled" type="number" class="form-control"
								id="stock-quantity" required="required" />
							<p>Product Quantity</p>
							<input type="number" class="form-control" id="product-quantity"
								required="required" />
							<p>Price @Unit</p>
							<input disabled="disabled" class="form-control"
								id="product-price" required="required" />
							<p>Expiry Date</p>
							<input disabled="disabled" type="date" class="form-control"
								id="product-exp-date" />
							<p></p>
							<button class="btn btn-submit" id="add-product"
								onclick="addToChart()">Add</button>
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
		<div>
			<button class="btn btn-primary" id="btn-send" onclick="send()">Submit
				Transaction</button>
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
		var currentCustomer;
		var inputProductField = _byId("input-product");
		var stockIdField = _byId("stock-id");
		var totalPriceLabel = _byId("total-price");
		var productListDropDown = _byId("product-dropdown");
		var productFlowTable = _byId("product-flows");
		var tableReceipt = _byId("table-receipt");
		
		var inputCustomerField = _byId("input-customer");
		var customerListDropDown = _byId("customer-dropdown");
		
		
		function send() {
			if(!confirm("Are You Ready To Submit Transaction?"))
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
				["Customer", transaction.customer.name,""]
			];
			
			const tbody  = createTBodyWithGivenValue(tableColumns); 
			tableReceipt.innerHTML = tbody.innerHTML; 
			
			processReceipt(transaction); 
		}		

		function loadCustomerList() {
			const filterValue = inputCustomerField.value;
			
			loadStakeHolderList(customerListDropDown, 'customer', 'name', filterValue, 
					function(entity) {
							inputCustomerField.value = entity.name;
							_byId("customer-name").innerHTML = entity.name;
							/* _byId("customer-address").innerHTML = entity.address;
							_byId("customer-contact").innerHTML = entity.contact; */
							currentCustomer = entity;
			}); 
		}

		function loadPrductList() {
			productListDropDown.innerHTML = "";
			var requestObject = {
				"product" : {
					"name" : inputProductField.value
				}
			};

			loadEntityList("<spring:url value="/api/transaction/stocks" />",
					requestObject, function(entities) {
						for (let i = 0; i < entities.length; i++) {
							const flowEntity = entities[i];
							const entity = flowEntity.product;
							const option = createHtmlTag({
								tagName: 'option',
								value: entity['id'],
								innerHTML: flowEntity.id + "-" + entity["name"],
								onclick : function() {
									setCurrentProduct(flowEntity, true);
								}
							})
							 
							productListDropDown.append(option);
						}
					});
		}

		/***COMPONENT OPERATION***/
		
		var priceField = _byId("product-price");
		var quantityField = _byId("stock-quantity");
		var inputQuantityField = _byId("product-quantity");
		var expiryDateField = _byId("product-exp-date");

		function addToChart() {
			if (currentProduct == null) {
				alert("Product is not specified!");
				return;
			}
			if (inputQuantityField.value*1 > quantityField.value*1) {
				alert("Quantity insufficient");
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
				"count" : inputQuantityField.value,
				"expiryDate" : expiryDateField.value,
				"flowReferenceId":stockIdField.value

			};

			productFlows.push(productFlow);
			populateProductFlow(productFlows);
			console.log("Product Flows", productFlows);
			currentProduct = null;
			currentProductFlow = null;
			clearProduct();
		}
		
		function calculateChange(){
			var totalPrice=_byId("total-price-label").value; 
			var puchaseValue = _byId("purchase-price").value;
			_byId("total-change-label").value = puchaseValue - totalPrice;
		}

		function clearProduct() {
			clearElement(inputProductField, priceField, quantityField, inputQuantityField, expiryDateField, stockIdField);
			clearElement("unit-name", "product-dropdown", "total-change-label", "purchase-price"); 
		}

		function setCurrentProduct(entity, loadNewStock) {
			inputProductField.value = entity.product.name;
			_byId("unit-name").innerHTML = entity.product.unit.name;
			currentProduct = entity.product;
			priceField.value = beautifyNominal(entity.product.price);
			inputQuantityField.value = entity.count;
			//let expDate=new Date(entity.expiryDate);
			
			expiryDateField.value =entity.expiryDate; //toDateInput(expDate);
			stockIdField.value = entity.id;

			//get remaining
			let ID = entity.id;
			if(entity.flowReferenceId != null){
				ID = entity.flowReferenceId;
			}
			if(loadNewStock)
				getStock(ID, false);

		}
		
		function  stockInfo(){
			getStock(stockIdField.value,true);
		}
		
		function  getStock(ID, updateProduct){
			var requestObject = {
					"productFlow" : {
						"id" : ID
					}
				}

				postReq("<spring:url value="/api/transaction/stockinfo" />",
						requestObject,
						function(xhr) {
							var response = (xhr.data);
							var code = response.code;
							if (code == "00") {
								quantityField.value = response.productFlowStock.remainingStock;
								if(updateProduct){
									setCurrentProduct(response.productFlowStock.productFlow, false);
								}
							} else {
								alert("server error");
							}
						});
		} 

		function populateProductFlow(productFlows) {
			doPopulateProductFlow(productFlows, function(i, productFlow, row){
			 
				row.append(createCell((i * 1 + 1) + ""));
				row.append(createCell(productFlow.id));
				row.append(createCell(productFlow.product.name));
				row.append(createCell(productFlow.expiryDate));
				row.append(createCell(productFlow.count));
				row.append(createCell(beautifyNominal(productFlow.price)));
				row.append(createCell(productFlow.flowReferenceId));
				
			});
		}

		function setCurrentProductFlow(entity) {
			currentProductFlow = entity;
		
			priceField.value = entity.price;
		//	quantityField.value = entity.productFlowStock.remainingStock;
			expiryDateField.value = entity.expiryDate;
			setCurrentProduct(entity, true);
		}
	</script>
<c:if test="${requestCode != null }">
	<script type="text/javascript">
		const requestTransactionCode = "${requestCode}";
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
			            "type":"OUT"
			        }
			    }
			};
		doGetById("<spring:url value="/api/entity/get" />", requestObject, function(entity){
			showReceipt(entity);
		});	
		
	</script>
</c:if>