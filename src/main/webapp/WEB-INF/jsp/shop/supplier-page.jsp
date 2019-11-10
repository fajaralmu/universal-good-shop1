<%@page import="com.fajar.entity.User"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<style>
.card-title:hover {
	cursor: pointer;
}
</style>
<div class="content">
	<p></p>
	<div id="catalog-content">
		<h2>Our Suppliers</h2>
		<p></p>

		<table
			style="layout: fixed; border-collapse: separate; border-spacing: 5px;">
			<tr valign="top" style="width: 100%">
				<td style="width: 20%">
					<!-- FILTER -->
					<div>
						<p>Name</p>
						<input id="search-name" class="form control" />
						 
						<p>Order By</p>
						<select class="form control" id="select-order">
							<option value="00" selected="selected">NONE</option>
							<option value="name-asc">Name [A-Z]</option>
							<option value="name-desc">Name [Z-A]</option>
							 
						</select> 
						<p>Display per Page</p>
						<select class="form control" id="select-limit">
							<option value="10" selected="selected">10</option>
							<option value="15">15</option>
							<option value="20">20</option>
							 
						</select> 
						
						 
					</div>
					<button class="btn btn-outline-primary" onclick="loadEntity()">Search</button>
				</td>
				<td style="width: 80%">
					<!-- PAGINATION -->
					<nav>
						<ul class="pagination" id="navigation-panel"></ul>
					</nav>
					<div id="catalog-panel" class="row"></div>
				</td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">


	var page = 0;
	var limit = 8;
	var totalData = 0;
	var orderBy = null;
	var orderType = null;
	var defaultOption = "${defaultOption}";
	
	//filted
	var selectOrder = document.getElementById("select-order");

	//elements
	var navigationPanel = document.getElementById("navigation-panel");
	var catalogPanel = document.getElementById("catalog-panel");
	var nameFilter = document.getElementById("search-name");
	 
	 
   

	 

	function populateCatalog(entities) {
		catalogPanel.innerHTML = "";
		for (let i = 0; i < entities.length; i++) {
			let entity = entities[i];

			//create col
			let colDiv = createDiv("col-" + entity.id, "col-sm-3");
			//create card
			let cardDiv = createDiv("card-" + entity.id, "card");
			cardDiv.style.width = "100%";
			cardDiv.style.backgroundColor = entity.color;
			cardDiv.style.color = entity.fontColor;
			//create icon tag
			let iconUrl = entity.iconUrl;

			let src = "${host}/${contextPath}/${imagePath}/"
					+ iconUrl;
			let iconImage = createImgTag("icon-" + entity.id, "card-img-top",
					"100", "150", src);
			iconImage.setAttribute("alt", entity.name);

			//card body
			let cardBody = createDiv("card-body-" + entity.id, "card-body");
			/* <div class="card-body"> */
			//card  title
			let cardTitle = createHeading("h5", "title-" + entity.id,
					"card-title", entity.name);
		 

			//list group
			let listGroup = createElement("ul", "list-group-" + entity.id,
					"list-group");
			listGroup.style.color = "#000000";
			//item list #1
			let listItemCount = createElement("li", "list-item-count-"
					+ entity.id,
					"list-group-item d-flex justify-content-between align-items-center");
			listItemCount.innerHTML = "Address";

			//item list #2
			let listItemPrice = createElement("li", "list-addr-"
					+ entity.id,
					"list-group-item d-flex justify-content-between align-items-center");
			listItemPrice.innerHTML =entity.address;
			listGroup.append(listItemCount);
			listGroup.append(listItemPrice);
			
			

			//populate cardbody
			cardBody.append(cardTitle);
			cardBody.append(listGroup);
			 
			//populate overall card
			cardDiv.append(iconImage);
			cardDiv.append(cardBody);

			//populate column
			colDiv.append(cardDiv);
			catalogPanel.append(colDiv);
		}
	}

	function loadEntity(page) {
		this.limit = document.getElementById("select-limit").value;
		if(this.limit > 20 || this.limit < 0){
			alert("Woooww.. our server will be confused with your choice");
			this.limit = 10;
			return;
		}
		if (page < 0) {
			page = this.page;
		}
		var selectedOrder = selectOrder.value;
		if(selectedOrder != null && selectedOrder != "00"){
			this. orderBy = selectedOrder.split("-")[0];
			this.orderType = selectedOrder.split("-")[1];
		}else{
			this. orderBy  = null;
			this.orderType = null;
		}
		var requestObject = {
			"entity" : "supplier",
			"filter" : {
				"limit" : this.limit,
				"page" : page,
				"orderBy" : orderBy,
				"orderType" : orderType,
				"fieldsFilter" : {
					"name" : nameFilter.value,
					 
				}
			}
		};
		/* if (categoryFilter.value != "00") {
			requestObject["filter"]["fieldsFilter"]["category,id[EXACTS]"] = categoryFilter.value;
		} */

		doLoadEntities("<spring:url value="/api/public/get" />", requestObject,
				function(response) {
					if(response.code != "00"){
						alert("Data Not Found");
						return;
					}
					var entities = response.entities;
					totalData = response.totalData;
					this.page = response.filter.page;
					populateCatalog(entities);
					updateNavigationButtons();
				});

	}

	function updateNavigationButtons() {
		createNavigationButtons(this.navigationPanel, this.page,
				this.totalData, this.limit, this.loadEntity);
	}

	/* if (defaultOption != "") {
		loadDetail(defaultOption);
		defaultLocation = defaultLocation.replace(defaultOption, "");
		defaultLocation = defaultLocation.replace("/" + defaultOption, "");
	} */

	loadEntity(page);
</script>
