


function infoLoading() {
	document.getElementById("loading-div").innerHTML = 
		"<img width='60px'  src=\""+ctxPath+"/res/img/loading-disk.gif\" />";
}

function infoDone() {
	document.getElementById("loading-div").innerHTML = "";
}

/** ***************COMPONENT*************** */
function createAnchor(id, html, url){
	var a = document.createElement("a");
	a.id = id;
	a.innerHTML = html;
	a.href = url
	return a;
}

function createNavigationButton(id, html, callback){
	var btn= createAnchor(id,html, "#");
	btn.className = "page-link";
	if(callback != null)
		btn.onclick = function(){
			callback(id);
		}
	var li = document.createElement("li");
	li.className = "page-item";
	li.append(btn);
	return li;
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

function createInputText(id, className){
	let input = document.createElement("input");
	input.id = id;
	input.setAttribute("class",className);
	return input;
}

function hide(id){
	document.getElementById(id).style.display = "none";
}

function show(id){
	document.getElementById(id).style.display = "block";
}
	 

function toDateInput(date){
	let dateStr  ="";
	let yearStr=date.getFullYear();
	let monthStr =( date.getMonth()+1) >=10?( date.getMonth()+1):"0"+( date.getMonth()+1);
	let dayStr =( date.getDate()+1) >=10?( date.getDate()+1):"0"+( date.getDate()+1);
	dateStr = yearStr+"-"+monthStr+"-"+dayStr;
	
	return dateStr;
	
};

function toBase64(file, callback){
	const reader = new FileReader();
    reader.readAsDataURL(file.files[0]);
    reader.onload = () => callback(reader.result);
    reader.onerror = error => {
    	alert("Error Loading File");
    }
}

function createDiv(id, className){
	let div = createElement("div", id, className); 
	return div;
}

function createInput(id, className, type){
	let div = createElement("input", id, className); 
	div.type = type; 
	return div;
}

function createOption(value, html){
	let option = createElement("option", null, null);
	option.value = value;
	option.innerHTML = html;
	return option;
}

function createHeading(tag ,id, className, html){
	let option = createElement(tag,id, className);
	option.innerHTML = html;
	return option;
}

function createElement(tag, id, className){
	let div = document.createElement(tag);
	if(className!=null)
		div.className = className;
	if(id != null)
		div.id  = id;
	return div;
}

function createImgTag(id, className, w, h, src){
	let img = createElement("img", id, className);
	img.width = w;
	img.height = h;
	
	img.src = src;
	
	return img;
}

function createTableHeaderByColumns(columns){
	console.log("Headers", columns);
	
	let row = createElement("tr","th-header-detail",null);
	 
	row.append(createCell("No"));
	for (var i = 0; i < columns.length; i++) {
		var column = columns[i];
		column = column.toUpperCase();
		column = column.replace("."," ");
		row.append(createCell("<b>"+column+"</b>"));
	}
	
	return row;
}

function createTableBody(columns, entities){
	// let tbody = createElement("tbody", "tbody-detail", "tbody-detail");
	let rows = [];
	for (let j = 0; j < entities.length; j++) {
		let entity = entities[j];
		
		let row = createElement("tr","tr-body-detail-"+j,null);
		 
		row.append(createCell(j+1));
		for (let i = 0; i < columns.length; i++) {
			let column = columns[i];
			let refField = column.split(".");
			let entityValue = entity[column];
			
			let cell = createCell("");
			cell.setAttribute("name",column); 
			
			if(refField.length>1 && entity[refField[0]] !=null){
			 	entityValue = entity[refField[0]][refField[1]];
				cell.setAttribute("name", refField[1]);
				 
			}
			
			cell.innerHTML = entityValue;
			row.append(cell);
		}
		rows.push(row);
	}
	return rows;
}

function createFilterInputDate(inputGroup, fieldName, callback){
	//input day
	let inputDay = createInputText(
			"filter-" + fieldName + "-day", "filter-field form-control"); 
	inputDay.setAttribute("field", fieldName + "-day");
	inputDay.style.width = "30%";
	inputDay.onkeyup = function() {
		callback();
	}
	//input month
	let inputMonth = createInputText("filter-" + fieldName
			+ "-month", "filter-field form-control");
	inputMonth.setAttribute("field", fieldName + "-month");
	inputMonth.style.width = "30%"; 
	inputMonth.onkeyup = function() {
		callback();
	}
	//input year
	let inputYear = createInputText(
			"filter-" + fieldName + "-year", "filter-field form-control");
	inputYear.setAttribute("field", fieldName + "-year"); 
	inputYear.style.width = "30%";
	inputYear.onkeyup = function() {
		callback();
	}
	inputGroup.append(inputDay);
	inputGroup.append(inputMonth);
	inputGroup.append(inputYear);
	return inputGroup;
}

/** ******NAVIGATION******loadEntity** */
function createNavigationButtons(navigationPanel,currentPage,totalData,limit,buttonClickCallback) {
	navigationPanel.innerHTML = "";
	var buttonCount = Math.ceil(totalData / limit);
	let prevPage = currentPage == 0 ? 0 : currentPage - 1;
	// prev and first button
	navigationPanel.append(createNavigationButton(0, "|<",buttonClickCallback));
	navigationPanel.append(createNavigationButton(prevPage, "<",buttonClickCallback));

	/* DISPLAYED BUTTONS */
	let displayed_buttons = new Array();
	let min = currentPage - 2;
	let max = currentPage + 2;
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
		if (!lastSeparated && currentPage < i - 2
				&& (i * 1 + 1) == (buttonCount - 1)) {
			// console.log("btn id",btn.id,"MAX",max,"LAST",(jumlahTombol-1));
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

		let button = createNavigationButton(i, buttonValue,buttonClickCallback);
		if (i == page) {
			button.className = button.className.replace("active", "");
			button.className = button.className + " active ";
		}
		navigationPanel.append(button);
	}

	let nextPage = currentPage == buttonCount - 1 ? currentPage : currentPage + 1;
	// next & last button
	navigationPanel.append(createNavigationButton(nextPage, ">",buttonClickCallback));
	navigationPanel.append(createNavigationButton(buttonCount - 1, ">|",buttonClickCallback));
	return navigationPanel;
}

