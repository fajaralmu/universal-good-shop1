
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<script type="text/javascript">
	var entityName = "${entityProperty.entityName}";
	var page = 0;
	var limit = 5;
	var totalData = 0;
	var imgElements = ${
		entityProperty.imageElementsJson
	};
	var currencyElements = ${
		entityProperty.currencyElementsJson
	};
	var dateElements = ${
		entityProperty.dateElementsJson
	};
	var fieldNames = ${
		entityProperty.fieldNames
	};
	var optionElements = ${
		options
	};
	var imagesData = {};
	var idField = "${entityProperty.idField}";
	var  editable = ${entityProperty.editable};
	
</script>

<!-- DETAIL ELEMENT -->
<div class="modal fade" id="modal-entity-detail" tabindex="-1"
	role="dialog" aria-labelledby="Entity Detail Modal"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="title-detail-modal">Detail</h5>
				<button type="button" class="close" data-dismiss="modal" onclick="$('#modal-entity-form').modal('show')"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body" style="width: 90%; height: 400px; margin: auto; overflow: scroll;">
				<table class="table" id="table-detail" style="layout: fixed">
				</table>
			</div>		
		 <div class="modal-footer">
        <button type="button" class="btn btn-secondary" onclick="$('#modal-entity-form').modal('show')" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<!-- INPUT FORM -->
<div class="modal fade" id="modal-entity-form" tabindex="-1"
	role="dialog" aria-labelledby="Entity Form Modal"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="exampleModalCenterTitle">${title }</h5>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body" style="height: 400px; overflow: scroll;">
				
				<div>
					<table style="layout: fixed">
						<tr>
							<td colspan="2"></td>
						</tr>
						<c:forEach var="element" items="${entityProperty.elements}">
							<tr valign="top">
								<td><label>${element.lableName }</label></td>
								<td><c:choose>
										<c:when test="${  element.type == 'fixedlist'}">
											<select class="input-field form-control" id="${element.id }"
												required="${element.required }"
												identity="${element.identity }"
												itemValueField="${element.optionValueName}"
												itemNameField="${element.optionItemName}">

											</select>
											<script>
												window["valueField_${element.id}"] = "${element.optionValueName}";
												window["itemField_${element.id}"] = "${element.optionItemName}";
												let options = ${
													element.jsonList
												};
												for (let i = 0; i < options.length; i++) {
													let option = document
															.createElement("option");
													let optionItem = options[i];
													option.value = optionItem["${element.optionValueName}"];
													option.innerHTML = optionItem["${element.optionItemName}"];
													document.getElementById(
															"${element.id }")
															.append(option);
												}
											</script>
										</c:when>
										<c:when test="${  element.type == 'dynamiclist'}">
											<input onkeyup="loadList(this)" name="${element.id }"
												id="input-${element.id }" class="form-control" type="text" />
											<br />
											<select style="width: 200px" class="input-field form-control"
												id="${element.id }" required="${element.required }"
												multiple="multiple" identity="${element.identity }"
												itemValueField="${element.optionValueName}"
												itemNameField="${element.optionItemName}"
												name=${element.entityReferenceClass}
											>

											</select>
											<script>
												window["valueField_${element.id}"] = "${element.optionValueName}";
												window["itemField_${element.id}"] = "${element.optionItemName}";
											</script>
										</c:when>
										<c:when test="${  element.type == 'textarea'}">
											<textarea class="input-field form-control"
												id="${element.id }" type="${element.type }"
												${element.required?'required':'' }
												identity="${element.identity }">
									</textarea>
										</c:when>
										<c:when test="${  element.showDetail}">
											<input detailfields="${element.detailFields}"
												showdetail="true" class="input-field" id="${element.id }"
												type="hidden" name="${element.optionItemName}"
												disabled="disabled" />
											<button id="btn-detail-${element.id }" class="btn btn-info"
												onclick="showDetail('${element.id }','${element.optionItemName}' )">Detail</button>
										</c:when>
										<c:when
											test="${ element.type=='img' && element.multiple == false}">
											<input class="input-field form-control" id="${element.id }"
												type="file" ${element.required?'required':'' }
												identity="${element.identity }" />
											<button id="${element.id }-file-ok-btn"
												class="btn btn-primary btn-sm"
												onclick="addImagesData('${element.id}')">ok</button>
											<button id="${element.id }-file-cancel-btn"
												class="btn btn-warning btn-sm"
												onclick="cancelImagesData('${element.id}')">cancel</button>
											<div>
												<img id="${element.id }-display" width="50" height="50" />
											</div>
										</c:when>
										<c:when
											test="${ element.type=='img' && element.multiple == true}">
											<div id="${element.id }" name="input-list"
												class="input-field">
												<div id="${element.id }-0-input-item"
													class="${element.id }-input-item">
													<input class="input-file" id="${element.id }-0" type="file"
														${element.required?'required':'' }
														identity="${element.identity }" />
													<button id="${element.id }-0-file-ok-btn "
														class="btn btn-primary btn-sm"
														onclick="addImagesData('${element.id}-0')">ok</button>
													<button id="${element.id }-0-file-cancel-btn"
														class="btn btn-warning btn-sm"
														onclick="cancelImagesData('${element.id}-0')">cancel</button>
													<button id="${element.id }-0-remove-list"
														class="btn btn-danger btn-sm"
														onclick="removeImageList('${element.id }-0')">Remove</button>
													<div>
														<img id="${element.id }-0-display" width="50" height="50" />
													</div>
												</div>
											</div>
											<button id="${element.id }-add-list"
												onclick="addImageList('${element.id }')">Add</button>
										</c:when>
										<c:when test="${ element.identity}">
											<input class="input-field form-control" disabled="disabled"
												id="${element.id }" type="text"
												${element.required?'required':'' }
												identity="${element.identity }" />
										</c:when>
										<c:otherwise>
											<input class="input-field form-control" id="${element.id }"
												type="${element.type }" ${element.required?'required':'' }
												identity="${element.identity }" />
										</c:otherwise>
									</c:choose></td>
							</tr>
						</c:forEach>
						 
					</table>
				</div>

				<!-- </div> -->
			</div>
			<div class="modal-footer">
				<c:if test="${entityProperty.editable == true }">
					<button id="btn-submit" onclick="submit()"
						class="btn btn-primary">Save Changes</button>
					<button id="btn-clear" onclick="clear()"
						class="btn btn-secondary">Clear</button>
				</c:if>
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
				
			</div>
		</div>
	</div>
</div>

<!-- CONTENT -->
<div class="content">
	<h2>${entityProperty.entityName}-Management</h2>
	<p></p>
	<c:if test="${entityProperty.editable == true }">
		<button type="btn-show-form" class="btn btn-primary"
			data-toggle="modal" data-target="#modal-entity-form">Show
			Form</button>
		<!-- <button id="btn-show-form" class="btn btn-info" onclick="show('modal-entity-form')">Show
			Form</button> -->
	</c:if>
	<p></p>
	
	<!-- PAGINATION -->
	<nav>
		<ul class="pagination" id="navigation-panel"></ul>
	</nav>

	<!-- DATA TABLE -->
	<div style="overflow: scroll; width: 100%; border: solid 1px">
		<table class="table" id="list-table" style="layout: fixed">
			<thead id="entity-th">
			</thead>
			<tbody id="entity-tb">
			</tbody>
		</table>
	</div>

</div>

<script type="text/javascript">
	var fields = document.getElementsByClassName("input-field");
	var filterFields = document.getElementsByClassName("filter-field");
	var entityTBody = document.getElementById("entity-tb");
	var entityTHead = document.getElementById("entity-th");
	var entitiesTable = document.getElementById("list-table");
	var detailTable = document.getElementById("table-detail");
	var filterField = document.getElementById("filter-field");
	var filterValue = document.getElementById("filter-value");
	var navigationPanel = document.getElementById("navigation-panel");
	var orderBy = null;
	var orderType = null;

	/*
		add single image
	*/
	function addImagesData(id) {
		let imageTag = document.getElementById(id + "-display");
		toBase64(document.getElementById(id), function(result) {
			let imageData = {
				id : result
			};
			imageTag.src = result;
			imagesData[id] = result;
			console.log("Images Data", imagesData);
		});
	}

	/*
		cancel single image
	*/
	function cancelImagesData(id) {
		document.getElementById(id).value = null;
		let imageTag = document.getElementById(id + "-display");
		imageTag.src = imageTag.getAttribute("originaldata");
		//remove from imagesData object
		imagesData[id] = null;
	} 

	//load dropdown list for multiple select
	function loadList(inputElement) {

		let element = document.getElementById(inputElement.name);
		element.innerHTML = "";
		//converter field
		let itemField = element.getAttribute("itemNameField");
		//foreign key field
		let valueField = element.getAttribute("itemValueField");
		let filterValue = inputElement.value;
		var requestObject = {
			"entity" : element.name,
			"filter" : {
				"page" : 0,
				"limit" : 10
			}
		};
		requestObject.filter.fieldsFilter = {};
		requestObject.filter.fieldsFilter[itemField] = filterValue;

		doLoadDropDownItems("<spring:url value="/api/entity/get" />", requestObject, function(entities){
			for (let i = 0; i < entities.length; i++) {
				let entity = entities[i];
				let option = document.createElement("option");
				option.value = entity[valueField];
				option.innerHTML = entity[itemField];
				option.onclick = function() {
					inputElement.value = option.innerHTML;
				}
				element.append(option);
			}
		});
		
	}

	function getById(entityId, callback) {
		var requestObject = {
			"entity" : entityName,
			"filter" : {
				"limit" : 1,
				"page" : 0,
				"exacts" : true,
				"contains" : false

			}
		};
		requestObject.filter.fieldsFilter = {};
		requestObject.filter.fieldsFilter[idField] = entityId;

		doGetById("<spring:url value="/api/entity/get" />", requestObject,callback);
		
	}

	function loadEntity(page) {
		if (page < 0) {
			page = this.page;
		}
		var requestObject = {
			"entity" : entityName,
			"filter" : {
				"limit" : limit,
				"page" : page,
				"orderBy" : this.orderBy,
				"orderType" : this.orderType

			}
		};
		requestObject.filter.fieldsFilter = {};
		for (let i = 0; i < filterFields.length; i++) {
			let filterField = filterFields[i];
			if (filterField.value != "") {
				let fieldName = filterField.getAttribute("field");
				let filterValue = filterField.value;
				let checkBoxExact = document.getElementById("checkbox-exact-"+fieldName);
				console.log("EXACT",checkBoxExact != null && checkBoxExact.checked);
				console.log("CHECKBOX",checkBoxExact);
				if(checkBoxExact != null && checkBoxExact.checked){
					fieldName = fieldName+"[EXACTS]";
				}
				requestObject.filter.fieldsFilter[fieldName] = filterValue;
			}
		}
		doLoadEntities("<spring:url value="/api/entity/get" />", requestObject, function(response){
			
			var entities = response.entities;
			if(entities == null){
				alert("Server Error!");
				return;
			}
			totalData = response.totalData;
			this.page = response.filter.page;
			populateTable(entities);
			updateNavigationButtons();
		});
		
	}

	function updateNavigationButtons() {
		createNavigationButtons(this.navigationPanel,this.page,this.totalData,this.limit,this.loadEntity);
	}

	
	//is image field
	function isImage(id) {
		for (var i = 0; i < imgElements.length; i++) {
			var array_element = imgElements[i];
			if (id == array_element) {
				return true;
			}
		}
		return false;
	}
	
	//is currency field
	function isCurrency(id) {
		for (var i = 0; i < currencyElements.length; i++) {
			var array_element = currencyElements[i];
			if (id == array_element) {
				return true;
			}
		}
		return false;
	}

	//is date field
	function isDate(id) {
		for (var i = 0; i < dateElements.length; i++) {
			var array_element = dateElements[i];
			if (id == array_element) {
				return true;
			}
		}
		return false;
	}

	//populate data table
	function populateTable(entities) {
		entityTBody.innerHTML = "";

		//CONTENT
		for (let i = 0; i < entities.length; i++) {
			let entity = entities[i];
			let row = document.createElement("tr");
			row.setAttribute("valign", "top");
			row.setAttribute("class", "entity-record");
			number = i * 1 + 1 + page * limit;
			row.append(createCell(number));
			for (let j = 0; j < fieldNames.length; j++) {
				let entityValue = entity[fieldNames[j]];
				//handle object type value
				if (typeof (entityValue) == "object" && entityValue != null) {
					console.log("TYPE ", typeof (entityValue), fieldNames[j]);
					let objectFieldName = window["itemField_" + fieldNames[j]];
					entityValue = entityValue[objectFieldName];
				}
				
				//handle date type value
				else if (isDate(fieldNames[j])) {
					entityValue = new Date(entityValue);
				}
				//handle if currency value
				//else if (isCurrency(fieldNames[j])) {
				else if(typeof (entityValue) == "number" && entityValue != null){
					entityValue = "<span style=\"font-family:consolas\">"+ beautifyNominal(entityValue) +"</span>";
				}
				//handle image type value
				else if (isImage(fieldNames[j])) {
					if (entityValue.split("~") != null) {
						entityValue = entityValue.split("~")[0];
					}
					entityValue = "<img width=\"30\" height=\"30\" src=\"${host}/${contextPath}/${imagePath}/"
							+ (entityValue) + "\" />";
				}//regular value
				else if (  entityValue != null) {
					
					//limit string characters count 
					if ( typeof (entityValue) == "string" && entityValue.length > 35) {
						entityValue = entityValue.substring(0, 35) + "...";
					}
				}
				row.append(createCell(entityValue));
			}
			let optionCell = createCell("");
			
			//button edit
			let buttonEdit = createButton("btn-edit-" + i, editable?"Edit":"Detail");
			buttonEdit.className = "btn btn-warning"
			buttonEdit.onclick = function() {
				alert("will Edit: " + entity[idField]);
				getById(entity[idField], function(entity) {
					populateForm(entity);
				});
			}
			/* row.onclick = function() {
				alert("will Edit: " + entity[idField]);
				getById(entity[idField], function(entity) {
					populateForm(entity);
				});
			} */
			let btnOptionGroup = createDiv("btn-group-option-"+ i,"btn-group btn-group-sm");
			btnOptionGroup.append(buttonEdit);
			
			//button delete
			if(editable){
				let buttonDelete = createButton("delete_" + i, "Delete");
				buttonDelete.className = "btn btn-danger";
				buttonDelete.onclick = function() {
					if (!confirm("will Delete: " + entity[idField])) {
						return;
					}
					deleteEntity(entity[idField]);
				}
				btnOptionGroup.append(buttonDelete);
			}
			
			
			optionCell.append(btnOptionGroup);
			row.append(optionCell);
			entityTBody.append(row);
		}
	}

	function createTableHeader() {
		//HEADER
		this.entityTHead.innerHTML = "";
		let row = document.createElement("tr");
		row.append(createCell("No"));
		for (let i = 0; i < fieldNames.length; i++) {
			let fieldName = fieldNames[i];
			let inputGroup = createDiv("input-group-"+fieldName,"input-group input-group-sm mb-3");
			let cell = createCell(fieldName);
			let input = createInputText("filter-" + fieldName, "filter-field form-control");
			input.setAttribute("field", fieldName); 
			input.onkeyup = function() {
				loadEntity();
			} 
			let isDateField = false;
			if (isDate(fieldName)) {
				inputGroup = createFilterInputDate(inputGroup, fieldName, loadEntity);
				isDateField = true;
			}
			if (!isDateField)
				inputGroup.append(input);
			cell.append(inputGroup); 
			
			//sorting button
			let btnSortGroup = createDiv("btn-group-sort-"+fieldName,"btn-group btn-group-sm");
			let ascButton = createButton("sort-asc-" + fieldName, "asc");
			let descButton = createButton("sort-desc-" + fieldName, "desc");
			ascButton.className = "btn btn-outline-secondary btn-sm";
			descButton.className = "btn btn-outline-secondary btn-sm";
			descButton.onclick = function() {
				 
				orderType = "desc";
				orderBy = fieldName;
				loadEntity(page);
			}
			ascButton.onclick = function() {
			 
				orderType = "asc";
				orderBy = fieldName;
				loadEntity(page);
			}
			btnSortGroup.append(ascButton);
			btnSortGroup.append(descButton);
			cell.append(btnSortGroup);
			
			//checkbox is exacts
			//let inputGroupExact = createDiv("input-group-exact-"+fieldName,"input-group-text");
			let checkBoxExact = createElement("input", "checkbox-exact-"+fieldName, "none");
			checkBoxExact.type="checkbox";
			checkBoxInfo = createElement("span","cb-info-"+fieldName,"none");
			checkBoxInfo.innerHTML = "Exact Search";
			//inputGroupExact.append(checkBoxExact);
			//let divPrependCheckBox = createDiv("input-group-prepend-"+fieldName, "input-group-prepend");
			//divPrependCheckBox.append(inputGroupExact);
			
			//let inputGroupCheckBox = createDiv("input-group mb-3"+fieldName, "input-group mb-3");
			cell.append(document.createElement("br"));
			cell.append(checkBoxExact);
			checkBoxInfo.setAttribute("style","font-size:0.7em");
			cell.append(checkBoxInfo);
			//cell.append(inputGroupCheckBox);
			
			row.append(cell);
		}
		row.append(createCell("Option"));
		entityTHead.append(row);
	}

	function createBr() {
		return document.createElement("br");
	}

	function populateForm(entity) {
		clear();
		for (let j = 0; j < fieldNames.length; j++) {
			let entityValue = entity[fieldNames[j]];
			let entityValueAsObject = entityValue;
			//element
			let elementField = document.getElementById(fieldNames[j]);

			let enableDetail = elementField.getAttribute("showdetail") == "true";
			let isMultipleSelect = elementField.nodeName == "SELECT"
				&& elementField.getAttribute("multiple") == "multiple"
			let isImageField = isImage(fieldNames[j]);
			let isDateField = isDate(fieldNames[j]);

			//handle object type value
			if (typeof (entityValue) == "object" && entityValue != null) {


				let objectValueName = window["valueField_" + fieldNames[j]]
				entityValue = entityValueAsObject[objectValueName];
				//handle multiple select
				if (isMultipleSelect) {
					let option = document.createElement("option");
					//foreign key field name
					objectValueName = elementField
							.getAttribute("itemvaluefield");
					option.value = entityValueAsObject[objectValueName];
					//converter field name
					let objectItemName = elementField
							.getAttribute("itemnamefield");
					option.innerHTML = entityValueAsObject[objectItemName];
					option.selected = true;
					elementField.append(option);
					//set input value same as converter field name
					let inputField = document.getElementById("input-"
							+ fieldNames[j]);
					inputField.value = entityValueAsObject[objectItemName];
				}
				//handle regular select
				else{
					elementField.value = entityValue;
				}
			} 
			//handle image type value
			else if (isImageField) {
				let displayElement = document.getElementById(fieldNames[j]
						+ "-display");
				let url = "${host}/${contextPath}/${imagePath}/";
				if (displayElement == null && entityValue != null) {
					document.getElementById(fieldNames[j]).innerHTML = "";
					let entityValues = entityValue.split("~");
					console.log(fieldNames[j], "values", entityValues);
					for (let i = 0; i < entityValues.length; i++) {
						let array_element = entityValues[i];
						doAddImageList(fieldNames[j], url + array_element,
								array_element);
					}
				} else {
					let resourceUrl = url + entityValue;
					displayElement.src = resourceUrl;
					displayElement.setAttribute("originaldata", resourceUrl);
					displayElement.setAttribute("originalvalue", entityValue);
				}
			}
			//handle regular value
			else if (!isMultipleSelect) {
				//datefield
				if (isDateField) {
					let date = new Date(entityValue);
					entityValue = toDateInput(date);
				} 
				//has detail values
				else if (enableDetail) {
					entityValue = entity[elementField.getAttribute("name")];
					elementField.setAttribute(
							elementField.getAttribute("name"), entityValue);
				}
				elementField.value = entityValue;

			}

		}
		//show("modal-entity-form");
		$('#modal-entity-form').modal('show');
	}

	function clear() {
		fields = document.getElementsByClassName("input-field");
		for (let i = 0; i < fields.length; i++) {
			let id = fields[i].id;
			let element = document.getElementById(id);
			if (element.nodeName == "SELECT"
					&& element.getAttribute("multiple") == "multiple") {
				element.innerHTML = "";
				document.getElementById("input-" + id).value = "";
			} else {
				element.value = null;
				element.value = "";
			}
		}
		imagesData = [];
	}

	function addImageList(id) {
		doAddImageList(id, null, null);
	}

	//add image to image list
	function doAddImageList(id, src, originalvalue) {
		let listParent = document.getElementById(id);//+"-input-list");
		//current item list elements
		let itemLists = document.getElementsByClassName(id + "-input-item");
		let length = 0;
		if (itemLists != null)
			length = itemLists.length;

		let index = length;
		if (index < 0) {
			index = 0;
		}
		
		//begin create new list item element
		let elmentIdAndIndex = id + "-" + index;
		//create list item
		let listItem = createDiv(elmentIdAndIndex + "-input-item", id + "-input-item");
		
		//create file input for choosing image
		let input = createInput(elmentIdAndIndex, "input-file", "file");
		//create image tag for displaying image
		let imgTag = createImgTag(elmentIdAndIndex + "-display", null, "50", "50", src);
		if (src != null) {
			//with full path
			imgTag.setAttribute("originaldata", src);
			//only value
			imgTag.setAttribute("originalvalue", originalvalue);
		}
		//button SET selected image
		let btnAddData = createButton(elmentIdAndIndex + "-file-ok-btn", "ok");
		btnAddData.className = "btn btn-primary btn-sm";
		btnAddData.onclick = function() {
			addImagesData(elmentIdAndIndex);
		}
		//button CANCEL selectedImage
		let btnCancelData = createButton(elmentIdAndIndex + "-file-cancel-btn", "cancel");
		btnCancelData.className = "btn btn-warning btn-sm";
		btnCancelData.onclick = function() {
			cancelImagesData(elmentIdAndIndex);
		}

		//button REMOVE list item
		let btnRemoveListItem = createButton(elmentIdAndIndex + "-remove-list", "remove");
		btnRemoveListItem.className = "btn btn-danger btn-sm";
		btnRemoveListItem.onclick = function() {
			removeImageList(elmentIdAndIndex);
		}
		//append file input
		listItem.append(input);
		
		//append buttons
		listItem.append(btnAddData);
		listItem.append(btnCancelData);
		listItem.append(btnRemoveListItem);
		
		//append image display
		let wrapperDiv = createDiv(elmentIdAndIndex + "-wrapper-img", "wrapper");
		wrapperDiv.append(imgTag);
		listItem.append(wrapperDiv);

		listParent.append(listItem);

	}

	function removeImageList(id) {
		if (!confirm("Are you sure want to remove this item?"))
			return;
		let element = document.getElementById(id);
		element.parentNode.remove(element);
	}
	function showDetail(elementId, field) {
		var requestObject = {
			'entity' : elementId,
			'filter' : {
				'limit' : 0,
				'orderBy' : null,
				'contains' : false,
				'exacts' : true,
				'orderType' : null,
				"fieldsFilter" : {}
			}
		};
		requestObject.filter.fieldsFilter[entityName] = document
				.getElementById(elementId).getAttribute(field);
		let detailFields = document.getElementById(elementId).getAttribute(
				"detailfields").split("~");
		console.log("request", requestObject);
		detailTable.innerHTML = "";

		doGetDetail("<spring:url value="/api/entity/get" />", requestObject,detailFields, populateDetailModal);
		
	}
	
	function populateDetailModal(entities,detailFields){
		//table detail header
		let tableHeader = createTableHeaderByColumns(detailFields); 
		//table detail body
		let bodyRows = createTableBody(detailFields, entities);
		detailTable.append(tableHeader);
		for (var i = 0; i < bodyRows.length; i++) {
			var row = bodyRows[i];
			detailTable.append(row);
		}
		$("#modal-entity-form").modal('hide');
		$('#modal-entity-detail').modal('show');
	}
	
	function setDefaultOption(){
		if(optionElements == null){
			return;
		}
		for (let optionElement in optionElements) {
			if(document.getElementById("filter-"+optionElement)==null)
				continue;
			document.getElementById("filter-"+optionElement).value = optionElements[optionElement];
		} 
		
	}
	
	createTableHeader();
	setDefaultOption();
	loadEntity(page);
	
</script> 
<c:if test="${entityProperty.editable == true }">
	<script type="text/javascript">
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
				//check if it is update or create operation
				if (field.getAttribute("identity") == "true"
						&& field.value != "" && field.value != null) {
					isNew = false;
				}
				if (field.nodeName == "SELECT") { //handle select element
					let idField = field.getAttribute("itemValueField");
					entity[fieldId] = {};
					entity[fieldId][idField] = field.value;
				} else if (isImage(fieldId)) { //handle image element
					
					//handle multiple images
					if (field.getAttribute("name") == "input-list") {
						let itemLists = document.getElementsByClassName(fieldId
								+ "-input-item");
						console.log(fieldId, "item list length",
								itemLists.length);
						if (itemLists == null || itemLists.length == 0) {
							continue;
						}
						let length = itemLists.length;
						entity[fieldId] = "";
						for (var j = 0; j < length; j++) {
							let elmentIdAndIndex = fieldId + "-" + j;
							let imgTag = document.getElementById(elmentIdAndIndex
									+ "-display");
							
							//check original image
							let originalValue = imgTag
									.getAttribute("originalvalue");
							if (originalValue != null) {
								entity[fieldId] += "{ORIGINAL>>"
										+ originalValue + "}";
							}

							//if current value has NOT been updated
							if (imagesData[fieldId + "-" + j] == null
									|| imagesData[fieldId + "-" + j].trim() == "") {
								entity[fieldId] += "~";
							} else {
							//if current value has been UPDATED
								entity[fieldId] += imagesData[elmentIdAndIndex] + "~";
							}
						}

					} 
					// single image
					else { 
						entity[fieldId] = imagesData[fieldId];
					}
				} else {//regular element
					entity[fieldId] = field.value;
				}
			}
			if (!isNew) {
				endPoint = "update";
			}
			requestObject[entityName] = entity;
			requestObject.entity = entityName;
			console.log("request object", requestObject);
			doSubmit("<spring:url value="/api/entity/" />" + endPoint, requestObject, function(){
				$("#modal-entity-form").modal('hide');
				loadEntity(this.page);
				clear();
			})
		};

		function deleteEntity(entityId) {
			doDeleteEntity("<spring:url value="/api/entity/delete" />", entityName, idField, entityId, function(){
				loadEntity(page);
			});
		}
	</script>
</c:if>