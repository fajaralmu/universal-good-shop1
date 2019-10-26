
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
	
</script>

<!-- DETAIL ELEMENT -->
<div class="modal fade" id="modal-entity-detail" tabindex="-1"
	role="dialog" aria-labelledby="Entity Detail Modal"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
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
	}

	function clearFilter() {
		filterValue.innerHTML = "";
		orderBy = null;
		orderType = null;
	}

	function loadList(inputElement) {

		let element = document.getElementById(inputElement.name);
		element.innerHTML = "";
		let itemField = element.getAttribute("itemNameField");
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

		postReq("<spring:url value="/api/entity/get" />", requestObject,
				function(xhr) {
					var response = (xhr.data);
					var entities = response.entities;
					if (entities != null && entities[0] != null) {
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

					} else {
						alert("data not found");
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

		postReq("<spring:url value="/api/entity/get" />", requestObject,
				function(xhr) {
					var response = (xhr.data);
					var entities = response.entities;
					if (entities != null && entities[0] != null) {
						callback(entities[0]);
					} else {
						alert("data not found");
					}
				});
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
				"orderBy" : orderBy,
				"orderType" : orderType

			}
		};
		requestObject.filter.fieldsFilter = {};
		for (let i = 0; i < filterFields.length; i++) {
			let filterField = filterFields[i];
			if (filterField.value != "") {
				let fieldName = filterField.getAttribute("field");
				requestObject.filter.fieldsFilter[fieldName] = filterField.value;
			}
		}
		postReq("<spring:url value="/api/entity/get" />", requestObject,
				function(xhr) {
					var response = (xhr.data);
					var entities = response.entities;
					totalData = response.totalData;
					this.page = response.filter.page;
					populateTable(entities);
					createNavigationButtons();
				});
	}

	function createNavigationButtons() {
		navigationPanel.innerHTML = "";
		var buttonCount = Math.ceil(totalData / limit);
		let prevPage = this.page == 0 ? 0 : this.page - 1;
		//prev and first button
		navigationPanel.append(createNavigationButton(0, "|<"));
		navigationPanel.append(createNavigationButton(prevPage, "<"));

		/* DISPLAYED BUTTONS */
		let displayed_buttons = new Array();
		let min = this.page - 2;
		let max = this.page + 2;
		for (let i = min; i <= max; i++) {
			displayed_buttons.push(i);
		}
		let firstSeparated = false;
		let lastSeparated = false;

		for (let i = 0; i < buttonCount; i++) {
			let buttonValue = i * 1 + 1;
			let included = false;
			for (let j = 0; j < displayed_buttons.length; j++) {
				if (displayed_buttons[j] == i && !included) {
					included = true;
				}
			}
			if (!lastSeparated && this.page < i - 2
					&& (i * 1 + 1) == (buttonCount - 1)) {
				//console.log("btn id",btn.id,"MAX",max,"LAST",(jumlahTombol-1));
				lastSeparated = true;
				var lastSeparator = document.createElement("span");
				lastSeparator.innerHTML = "...";
				navigationPanel.appendChild(lastSeparator);

			}
			if (!included && i != 0 && !firstSeparated) {
				firstSeparated = true;
				var firstSeparator = document.createElement("span");
				firstSeparator.innerHTML = "...";
				navigationPanel.appendChild(firstSeparator);

			}
			if (!included && i != 0 && i != (buttonCount - 1)) {
				continue;
			}

			let button = createNavigationButton(i, buttonValue);
			if (i == page) {
				button.className = button.className.replace("active", "");
				button.className = button.className + " active ";
			}
			navigationPanel.append(button);
		}

		let nextPage = this.page == buttonCount - 1 ? this.page : this.page + 1;
		//next & last button
		navigationPanel.append(createNavigationButton(nextPage, ">"));
		navigationPanel.append(createNavigationButton(buttonCount - 1, ">|"));
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
				if (typeof (entityValue) == "object" && entityValue != null) {
					console.log("TYPE ", typeof (entityValue), fieldNames[j]);
					let objectFieldName = window["itemField_" + fieldNames[j]];
					entityValue = entityValue[objectFieldName];
				} else if (!isDate(fieldNames[j]) && !isImage(fieldNames[j])
						&& typeof (entityValue) == "string"
						&& entityValue != null) {
					if (entityValue.length > 35) {
						entityValue = entityValue.substring(0, 35) + "...";
					}
				}
				
				if (isDate(fieldNames[j])) {
					entityValue = new Date(entityValue);
				} else if (isImage(fieldNames[j])) {
					if (entityValue.split("~") != null) {
						entityValue = entityValue.split("~")[0];
					}
					entityValue = "<img width=\"30\" height=\"30\" src=\"${host}/${contextPath}/${imagePath}/"
							+ (entityValue) + "\" />";
				}
				row.append(createCell(entityValue));
			}
			let optionCell = createCell("");
			
			//button edit
			let buttonEdit = createButton("edit_" + i, "Edit");
			buttonEdit.className = "btn btn-warning"
			buttonEdit.onclick = function() {
				alert("will Edit: " + entity[idField]);
				getById(entity[idField], function(entity) {
					populateForm(entity);
				});
			}
			row.onclick = function() {
				alert("will Edit: " + entity[idField]);
				getById(entity[idField], function(entity) {
					populateForm(entity);
				});
			}
			
			//button delete
			let buttonDelete = createButton("delete_" + i, "Delete");
			buttonDelete.className = "btn btn-danger";
			buttonDelete.onclick = function() {
				if (!confirm("will Delete: " + entity[idField])) {
					return;
				}
				deleteEntity(entity[idField]);
			}
			let btnOptionGroup = createDiv("btn-group-option-"+ i,"btn-group btn-group-sm");
			btnOptionGroup.append(buttonEdit);
			btnOptionGroup.append(buttonDelete);
			optionCell.append(btnOptionGroup);
			row.append(optionCell);
			entityTBody.append(row);
		}
	}

	function createTableHeader() {
		//HEADER
		entityTHead.innerHTML = "";
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
			let isDateField = false
			if (isDate(fieldName)) {
				input.id = "filter-" + fieldName + "-day";
				input.setAttribute("field", fieldName + "-day");
				input.style.width = "30%";
				let inputMonth = createInputText("filter-" + fieldName
						+ "-month", "filter-field form-control");
				inputMonth.setAttribute("field", fieldName + "-month");
				inputMonth.style.width = "30%"; 
				inputMonth.onkeyup = function() {
					loadEntity();
				}
				let inputYear = createInputText(
						"filter-" + fieldName + "-year", "filter-field form-control");
				inputYear.setAttribute("field", fieldName + "-year"); 
				inputYear.style.width = "30%";
				inputYear.onkeyup = function() {
					loadEntity();
				}
				inputGroup.append(input);
				inputGroup.append(inputMonth);
				inputGroup.append(inputYear);
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

			if (typeof (entityValue) == "object" && entityValue != null) {


				let objectValueName = window["valueField_" + fieldNames[j]]
				entityValue = entityValueAsObject[objectValueName];
				
				if (isMultipleSelect) {
					let option = document.createElement("option");
					objectValueName = elementField
							.getAttribute("itemvaluefield");
					option.value = entityValueAsObject[objectValueName];
					let objectItemName = elementField
							.getAttribute("itemnamefield");
					option.innerHTML = entityValueAsObject[objectItemName];
					option.selected = true;
					elementField.append(option);
					let inputField = document.getElementById("input-"
							+ fieldNames[j]);
					inputField.value = entityValueAsObject[objectItemName];
				}else{
					elementField.value = entityValue;
				}
			} else if (isImageField) {
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
			} else if (!isMultipleSelect) {
				if (isDateField) {
					let date = new Date(entityValue);
					entityValue = toDateInput(date);
				} else if (enableDetail) {
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
		let itemLists = document.getElementsByClassName(id + "-input-item");
		let length = 0;
		if (itemLists != null)
			length = itemLists.length;

		let index = length;
		if (index < 0) {
			index = 0;
		}
		let idIdx = id + "-" + index;
		let itemDiv = createDiv(idIdx + "-input-item", id + "-input-item");
		let input = createInput(idIdx, "input-file", "file");
		let imgTag = createImgTag(idIdx + "-display", null, "50", "50", src);
		if (src != null) {
			//with full path
			imgTag.setAttribute("originaldata", src);
			//only value
			imgTag.setAttribute("originalvalue", originalvalue);
		}
		let btnAddData = createButton(idIdx + "-file-ok-btn", "ok");
		btnAddData.className = "btn btn-primary btn-sm";
		btnAddData.onclick = function() {
			addImagesData(idIdx);
		}
		let btnCancelData = createButton(idIdx + "-file-cancel-btn", "cancel");
		btnCancelData.className = "btn btn-warning btn-sm";
		btnCancelData.onclick = function() {
			cancelImagesData(idIdx);
		}

		let btnRemoveList = createButton(idIdx + "-remove-list", "remove");
		btnRemoveList.className = "btn btn-danger btn-sm";
		btnRemoveList.onclick = function() {
			removeImageList(idIdx);
		}
		itemDiv.append(input);
		itemDiv.append(btnAddData);
		itemDiv.append(btnCancelData);
		itemDiv.append(btnRemoveList);
		let wrapperDiv = createDiv(idIdx + "-wrapper-img", "wrapper");
		wrapperDiv.append(imgTag);
		itemDiv.append(wrapperDiv);

		listParent.append(itemDiv);

	}

	function removeImageList(id) {
		if (!confirm("Are you sure want to remove this item?"))
			return;
		let element = document.getElementById(id);
		element.parentNode.remove(element);
	}
	function showDetail(id, field) {
		var requestObject = {
			'entity' : id,
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
				.getElementById(id).getAttribute(field);
		let detailFields = document.getElementById(id).getAttribute(
				"detailfields").split("~");
		console.log("request", requestObject);
		detailTable.innerHTML = "";

		postReq(
				"<spring:url value="/api/entity/get" />",
				requestObject,
				function(xhr) {
					var response = (xhr.data);
					var entities = response.entities;
					if (entities != null && entities[0] != null) {
						let tableHeader = createTableHeaderByColumns(detailFields);
						console.log("header", tableHeader);
						let bodyRows = createTableBody(detailFields, entities);
						detailTable.append(tableHeader);
						for (var i = 0; i < bodyRows.length; i++) {
							var row = bodyRows[i];
							detailTable.append(row);
						}
						$("#modal-entity-form").modal('hide');
						$('#modal-entity-detail').modal('show');
					} else {
						alert("data not found");
					}
				});
	}
	
	function setDefaultOption(){
		if(optionElements == null){
			return;
		}
		for (let optionElement in optionElements) {
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
				if (field.getAttribute("identity") == "true"
						&& field.value != "" && field.value != null) {
					isNew = false;
				}
				if (field.nodeName == "SELECT") {
					let idField = field.getAttribute("itemValueField");
					entity[fieldId] = {};
					entity[fieldId][idField] = field.value;
				} else if (isImage(fieldId)) {
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
							let idIdx = fieldId + "-" + j;
							let imgTag = document.getElementById(idIdx
									+ "-display");
							let originalValue = imgTag
									.getAttribute("originalvalue");
							if (originalValue != null) {
								entity[fieldId] += "{ORIGINAL>>"
										+ originalValue + "}";
							}

							if (imagesData[fieldId + "-" + j] == null
									|| imagesData[fieldId + "-" + j].trim() == "") {
								entity[fieldId] += "~";
							} else {
								entity[fieldId] += imagesData[idIdx] + "~";
							}
						}

					} else {
						entity[fieldId] = imagesData[fieldId];
					}
				} else {
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
							$("#modal-entity-form").modal('hide');
							loadEntity(page);
						} else {
							alert("FAILS");
						}
						clear();
					});
		};

		function deleteEntity(entityId) {
			console.log("Delete: ", entityId);
			var requestObject = {
				"entity" : entityName,
				"filter" : {

				}
			};
			requestObject.filter.fieldsFilter = {};
			requestObject.filter.fieldsFilter[idField] = entityId;

			postReq("<spring:url value="/api/entity/delete" />", requestObject,
					function(xhr) {
						var response = (xhr.data);
						var code = response.code;
						if (code == "00") {
							alert("success deleted");
							loadEntity(page);
						} else {
							alert("error deleting");
						}
					});
		}
	</script>
</c:if>