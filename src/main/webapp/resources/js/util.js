


function infoLoading() {
	document.getElementById("loading-div").innerHTML = 
		"<img width='60px'  src=\""+ctxPath+"/res/img/loading-disk.gif\" />";
}

function infoDone() {
	document.getElementById("loading-div").innerHTML = "";
}

/*****************COMPONENT****************/
function createAnchor(id, html, url){
	var a = document.createElement("a");
	a.id = id;
	a.innerHTML = html;
	a.href = url
	return a;
}

function createNavigationButton(id, html){
	var btn= createButton(id,html);
	btn.className = "btn btn-default";
	btn.onclick = function(){
		 loadEntity(id);
	}
	var li = document.createElement("li");
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

function createElement(tag, id, className){
	let div = document.createElement(tag);
	if(className!=null)
		div.className = className;
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