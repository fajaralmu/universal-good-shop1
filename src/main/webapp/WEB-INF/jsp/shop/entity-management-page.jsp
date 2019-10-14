
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1256">
<title>${entityProperty.entityName}</title>
<link rel="icon" href="<c:url value="/res/img/javaEE.ico"></c:url >"
	type="image/x-icon">
<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/style/style.css?version=1"></c:url> />

<script src="<c:url value="/res/js/ajax.js"></c:url >"></script>
<script src="<c:url value="/res/js/util.js"></c:url >"></script>
<script type="text/javascript">
	var entityName = "${entityProperty.entityName}";
	var page = 0;
	var limit = 5;
	var totalData = 0;
	var fieldNames = ${entityProperty.fieldNames};
	var idField = "${entityProperty.idField}";
	 
</script>
<style>
body {
	background-image: url("<c:url value="/ res/ img/ sea-bg.jpg "></c:url >");
	background-position: center;
	background-size: cover;
	background-repeat: no-repeat;
}

.login-field {
	width: 100%;
	height: 15%;
	border: none;
	color: green;
}

.tombol[name='login'] {
	border: solid 3px orange;
	margin: auto;
	width: 40%;
	padding: 5px;
	margin: auto;
	color: white;
	background-color: orange;
	font-size: 20px;
	transition-duration: 0.4s;
}

.tombol[name='login']:hover {
	color: orange;
	background-color: rgba(255, 255, 255, 0.8);
	cursor: pointer;
}

.wrapper-login-form {
	margin-left: 65%;
	box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0
		rgba(0, 0, 0, 0.19);
	text-align: center;
	margin-right: 10%;
	margin-top: 10%
}
</style>
</head>
<body>
	<div id="loading-div"></div>
	<h2>${entityProperty.entityName}Management</h2>
	<div class="body">
		<div id="entity-input-form" class="form">
			<table style="layout: fixed">
				<c:forEach var="element" items="${entityProperty.elements}">
					<tr valign="top">
						<td><label>${element.lableName }</label></td>
						<td><c:choose>
								<c:when test="${  element.type == 'fixedlist'}">
									<select class="input-field" id="${element.id }"
										required="${element.required }"
										identity="${element.identity }"
										itemValueField="${element.optionValueName}"
										itemNameField="${element.optionItemName}">

									</select>
									<script>
											var valueField_${element.id } ="${element.optionValueName}";
											var itemField_${element.id } = "${element.optionItemName}";
											let options = ${element.jsonList};
											for(let i=0;i<options.length;i++){
												let option  = document.createElement("option");
												let optionItem = options[i];
												option.value = optionItem[valueField_${element.id }];
												option.innerHTML = optionItem[itemField_${element.id }];
												document.getElementById("${element.id }").append(option);
											}
										</script>
								</c:when>
								<c:when test="${  element.type == 'textarea'}">
									<textarea class="input-field" id="${element.id }"
										type="${element.type }" required="${element.required }"
										identity="${element.identity }">
							</textarea>
								</c:when>
								<c:when test="${ element.identity}">
									<input class="input-field" disabled="disabled"
										id="${element.id }" type="text"
										required="${element.required }"
										identity="${element.identity }" />
								</c:when>
								<c:otherwise>
									<input class="input-field" id="${element.id }"
										type="${element.type }" required="${element.required }"
										identity="${element.identity }" />
								</c:otherwise>
							</c:choose></td>
					</tr>
				</c:forEach>
				<tr>
					<td>
						<button id="btn-submit" onclick="submit()" class="btn btn-ok">Submit</button>
					</td>
					<td>
						<button id="btn-clear" onclick="clear()" class="btn btn-ok">Clear</button>
					</td>
				</tr>
			</table>

		</div>
		<div id="filter-panel">
			<p>Filter by</p>
			<select id="filter-field">
				<c:forEach var="element" items="${entityProperty.elements}">
					<option value="${element.id }">${element.id }</option>
				</c:forEach>
			</select>
			<p>Filter value</p>
			<input onkeyup="loadEntity()" id="filter-value" /> <br>
			<button onclick="loadEntity(-1)" id="search">OK</button>
			<div id="navigation-panel"></div>
		</div>
		<table id="list-table" style="layout: fixed">

		</table>
	</div>
	<script type="text/javascript">
		var fields = document.getElementsByClassName("input-field");
		var entitiesTable = document.getElementById("list-table");
		var filterField = document.getElementById("filter-field");
		var filterValue = document.getElementById("filter-value");
		var navigationPanel = document.getElementById("navigation-panel");
		
		function withFilter(){
			return filterField.value !="" && filterValue.value != "";
		}
		
		function getById(entityId){
			var requestObject ={
					"entity":entityName,
					"filter":{
						"limit":1,
						"page":0,
						"exacts":true,
						"contains":false
						
					}
				};
		 	requestObject.filter.fieldsFilter = {};
			requestObject.filter.fieldsFilter[idField] = entityId;
			 
			postReq("<spring:url value="/api/entity/get" />"  ,
					requestObject, function(xhr) {
						var response = (xhr.data);
						var entities = response.entities;
						if(entities != null && entities[0] != null){
							populateForm(entities[0]);
						}else{
							alert("data not found");
						}
			});
		}
		
		function loadEntity(page){
			if(page < 0){
				page = this.page;
			}
			var requestObject ={
					"entity":entityName,
					"filter":{
						"limit":limit,
						"page":page,
						
					}
				};
			if(withFilter()){
				requestObject.filter.fieldsFilter = {};
				requestObject.filter.fieldsFilter[filterField.value] = filterValue.value;
			}
			
			postReq("<spring:url value="/api/entity/get" />"  ,
					requestObject, function(xhr) {
						var response = (xhr.data);
						var entities = response.entities;
						totalData = response.totalData;
						this.page = response.filter.page;
						populateTable(entities);
						createNavigationButtons();
			});
		}
		
		function createNavigationButtons(){
			navigationPanel.innerHTML = "";
			var buttonCount = Math.ceil(totalData/limit);
			navigationPanel.append(createNavigationButton(0, "|<")); 
			let prevPage = this.page == 0 ? 0: this.page -1;
			let prevButton =createNavigationButton(prevPage, "<");
			navigationPanel.append(prevButton); 
			for (let i = 0; i < buttonCount; i++) {
				let buttonValue = i*1+1;
				if(i == page){
					buttonValue = "<u>"+buttonValue+"</u>";
				}
				let button =createNavigationButton(i,buttonValue);
				 navigationPanel.append(button);
			}

			let nextPage = this.page == buttonCount-1 ? this.page : this.page+1;
			let nextButton =createNavigationButton(nextPage, ">");
			navigationPanel.append(nextButton);
			navigationPanel.append(createNavigationButton( buttonCount-1, ">|")); 
		}
		
		function createNavigationButton(id, html){
			var button = createButton(id,html);
			button.onclick = function(){
				 loadEntity(id);
			}
			return button;
		}
		
		function createButton(id, html){
			var button = document.createElement("button");
			button.id = id;
			button.innerHTML = html;
			return button;
		}
		
		function createCell(val){
			let column = document.createElement("td");
			column.innerHTML = val;
			return column;
		}
		
		function populateTable(entities){
			entitiesTable.innerHTML = "";
			//HEADER
			let row = document.createElement("tr");
			row.append(createCell("No"));
			for (let i = 0; i < fieldNames.length; i++) {
				let fieldName = fieldNames[i];
				row.append(createCell(fieldName));
			}
			entitiesTable.append(row);
			//CONTENT
			for(let i=0;i<entities.length;i++){
				let entity = entities[i];
				let row = document.createElement("tr");
				row.setAttribute("valign","top");
				number = i*1+1 + page*limit;
				row.append(createCell(number));
				for (let j = 0; j < fieldNames.length; j++) {
					let entityValue = entity[fieldNames[j]];
					if(typeof(entityValue) == "object" && entityValue != null){
						console.log("TYPE ",typeof(entityValue), fieldNames[j]);
						let objectFieldName = window["itemField_"+fieldNames[j]];
						entityValue = entityValue[objectFieldName];
					}
					row.append(createCell(entityValue));
				}
				let optionCell = createCell("");
				let buttonEdit = createButton("edit_"+i, "Edit");
				buttonEdit.onclick = function(){
					alert("will Edit: "+entity[idField]);
					getById(entity[idField]);
				}
				let buttonDelete = createButton("delete_"+i, "Delete");
				buttonDelete.onclick = function(){
					if(!confirm("will Delete: "+entity[idField])){
						return;
					}
					deleteEntity(entity[idField]);
				}
				optionCell.append(buttonEdit);
				optionCell.append(buttonDelete);
				row.append(optionCell);
				entitiesTable.append(row);
			}
		}
		
		function populateForm(entity){
			clear();
			for (let j = 0; j < fieldNames.length; j++) {
				let entityValue = entity[fieldNames[j]];
				if(typeof(entityValue) == "object" && entityValue != null){
					console.log("TYPE ",typeof(entityValue), fieldNames[j]);
					let objectValueName = window["valueField_"+fieldNames[j]]
					entityValue = entityValue[objectValueName];
				}
				document.getElementById(fieldNames[j]).value = entityValue;	
			}
		}
		
		function clear() {
			fields = document.getElementsByClassName("input-field");
			for (let i = 0; i < fields.length; i++) {
				let id = fields[i].id;
				document.getElementById(id).value = null;
				document.getElementById(id).value = "";
			}
		}

		function submit() {
			if (!confirm("Are You Sure ?")) {
				return;
			}
			var requestObject = {};
			var entity = {};
			var endPoint = "add";
			var isNew = true;
			for (var i = 0; i < fields.length; i++) {
				var field = fields[i];
				let fieldId = field.id;
				console.log("FIELD ", field);
				if (field.required
						&& (field.value == "" || field.value == null)
						&& field.getAttribute("identity") != "true") {
					alert("Field " + field.id + " must be filled! ");
					return;
				}
				if (field.getAttribute("identity") == "true"
						&& field.value != "" && field.value != null) {
					isNew = false;
				}
				if(field.nodeName == "SELECT"){
					let idField = field.getAttribute("itemValueField");
					entity[fieldId] = {};
					entity[fieldId][idField] = field.value;
				}else{
					entity[fieldId] = field.value;
				}
			}
			if (!isNew) {
				endPoint = "update";
			}
			requestObject[entityName] = entity;
			requestObject.entity = entityName;
			console.log("request object", requestObject);
			postReq("<spring:url value="/api/entity/" />" + endPoint,
					requestObject, function(xhr) {
						var response = (xhr.data);
						if (response != null && response.code == "00") {
							alert("SUCCESS");
							loadEntity(page);
						} else {
							alert("FAILS");
						}
						clear();
					});
		};
		
		loadEntity(page);
	</script>
</body>
</html>