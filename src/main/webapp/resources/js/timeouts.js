const timeOuts = {};

function incrementHeightAsync(imageElement, maxHeight, incrementBy,
		finalHandler) {
	const id = new Date().getMilliseconds() + "_" + incrementBy + "_"
			+ maxHeight;
	timeOuts[id] = setTimeout(function() {
		incrementImageHeight(imageElement, id, maxHeight, incrementBy,
				finalHandler);
	}, 1);
}

function incrementImageHeight(icon, id, maxHeight, incrementBy, finalHandler) {
	const modifyOperation = function(e, height) {
		const newVal = e.height + incrementBy;
		var test = incrementBy < 0 ? e.height <= maxHeight
				: e.height >= maxHeight;

		if (test) {

			e.setAttribute("animating", "false");
			return null;
		}
		e.setAttribute("animating", "true");
		return newVal;
	};

	animateObjAttribute(icon, "height", id, modifyOperation, finalHandler);
}

// modifyOperation returns null if will end timeout, otherwise returns result
// value of the attribute
function animateObjAttribute(element, attribute, id, modifyOperation,
		finalHandler) {

	const attributeVal = element.getAttribute(attribute);
	var newValue = modifyOperation(element, attributeVal);

	if (newValue == null) {
		clearTimeout(timeOuts[id]);
		if (finalHandler) {
			finalHandler();
		}
	} else {
		element.setAttribute(attribute, newValue);

		timeOuts[id] = setTimeout(function() {
			animateObjAttribute(element, attribute, id, modifyOperation,
					finalHandler);
		}, 1);
	}
}

// modifyOperation returns null if will end timeout, otherwise returns result
// value of the attribute
function animateObjStyle(element, attr, id, modifyOperation, finalHandler) {

	const attributeVal = element.style[attr];
	var newValue = modifyOperation(element, attributeVal);
	console.debug("newValue: ", newValue);
	if (newValue == null) {
		clearTimeout(timeOuts[id]);
		if (finalHandler) {
			finalHandler();
		}
	} else {
		element.style[attr] = (newValue);
		
		setTimeout(function() {
			animateObjStyle(element, attr, id, modifyOperation, finalHandler);
		}, 1);
	}
}

function updateWidthAsync(el, maxVal, incrementBy, finalHandler) {

	const id = new Date().getMilliseconds() + "_" + incrementBy + "_"
			+ maxVal;
	timeOuts[id] = setTimeout(function() {
		
		updateStyleAsync(el, id, 'width', maxVal, incrementBy, finalHandler);
		
	}, 1);
}

function updateStyleAsync(el, id, styleAttr, maxVal, incrementBy, finalHandler) {
	/*const id = new Date().getMilliseconds() + "_" + incrementBy + "_"
			+ maxHeight;*/
	console.debug("updateStyleAsync");
	const modifyOpr = function(el, styleVal) {
		console.debug("styleVal: ", styleVal);
		var withPercentage = false;
		var withPixel = false;
		
		if(styleVal.includes('%')){
			styleVal = styleVal.replace('%', '');
			withPercentage = true;
		}
		if(styleVal.includes('px')){
			styleVal = styleVal.replace('px', '');
			withPixel = true;
		}
		
		styleVal = +styleVal;
		console.debug('styleVal after', styleVal, "max val ", maxVal, incrementBy);
		var end;
		var retVal;
		
		if (incrementBy > 0) {
			end = styleVal < maxVal; 
		} else {
			end = styleVal > maxVal; 
		}
		
		retVal = styleVal + incrementBy;
		console.debug("end", end);
		
		if (!end) {
			return null;
		}
		
		if(withPercentage){
			retVal = retVal + '%';
		}
		if(withPixel){
			retVal = retVal + 'px';
		}
		return retVal;
	}
	timeOuts[id] = setTimeout(function() {
		animateObjStyle(el, styleAttr, id, modifyOpr, finalHandler);
	}, 1);
}