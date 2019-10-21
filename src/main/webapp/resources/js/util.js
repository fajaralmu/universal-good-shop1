


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
	 