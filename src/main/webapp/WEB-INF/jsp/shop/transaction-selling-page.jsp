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
		<h2>Selling</h2>
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
							<p hidden="true">Expiry Date</p>
							<input hidden="true" disabled="disabled" type="date" class="form-control"
								id="product-exp-date" />
							<p></p>
							<button class="btn btn-submit" id="add-product"
								onclick="addToCart()">Add</button>
						</div>
					</div>
				</td>
				<td>
					<jsp:include page="../transaction-selling/customer-form.jsp"></jsp:include>
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
 
		function loadPrductList() {
			productListDropDown.innerHTML = "";
			var requestObject = {
				'entity':'product',
				'filter':{
					'fieldsFilter' : { 	"name" : inputProductName.value }
				}
			};

			loadEntityList(ENTITY_GET_URL,
					requestObject, function(entities) {
						for (let i = 0; i < entities.length; i++) {
							
							const product = entities[i];
							const option = createHtmlTag({
								tagName: 'option',
								value: product['id'],
								innerHTML:  product['name'],
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
			const inputQty = +inputQuantityField.value;
			
			if (!inputQty || inputQty > quantityField.value*1) {
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

			productFlows.push(productFlow);
			populateProductFlow(productFlows);
			console.log("Product Flows", productFlows);
			currentProduct = null;
			currentProductFlow = null;
			clearProduct();
		}
		
		function calculateChange(){
			const totalPrice=byId("total-price-label").value; 
			const puchaseValue = byId("purchase-price").value;
			byId("total-change-label").value = puchaseValue - totalPrice;
		}

		function clearProduct() {
			clearElement(inputProductName, priceField, quantityField, inputQuantityField, expiryDateField);
			clearElement("unit-name", "product-dropdown", "total-change-label", "purchase-price"); 
			
			//quantityField.value = 0;
			//priceField.value = 0;
			//quantityField.value = 0; 
		}

		function setCurrentProduct(entity, loadNewStock) {
			inputProductName.value = entity.product.name;
			unitNameLabel.innerHTML = entity.product.unit.name;
			
			priceField.value = beautifyNominal(entity.product.price);
			quantityField.value = entity.product.count;
			
			if(!entity.count){
				inputQuantityField.value = 0; 
			}else{
				inputQuantityField.value = entity.count;
			}
			
			currentProduct = entity.product; 

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
		
		function getProductFlow(product){
			const requestObject = {
					"entity" : "product",
					"filter" : { 
						'limit':20,
						"fieldsFilter" : {
							"code[EXACTS]" : product.code,
							"withStock" : true
						}
					}
				}; 
				doLoadEntities("<spring:url value="/api/public/get" />", requestObject, function(res) {
					if (!res || res.code != "00") {
						alert("Data Not Found");
						return;
					}
					const entities = res.entities;
				 	const productDetailed = entities[0];
				 	const productFlowObj = {
				 		product: 	productDetailed
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