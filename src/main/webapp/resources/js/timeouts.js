const timeOuts = {};

function incrementHeightAsync(imageElement, maxHeight, incrementBy,
		finalHandler) {
	const id = new Date().getMilliseconds() + "_" + incrementBy + "_"
			+ maxHeight;
	timeOuts[id] = setTimeout(function() {
		incrementImageHeight (imageElement, id, maxHeight, incrementBy,
				finalHandler);
	}, 1);
}

function incrementImageHeight (icon, id, maxHeight, incrementBy, finalHandler) {
	const modifyOperation = function(e, height) {
		const newVal = e.height + incrementBy;
		var test = incrementBy < 0 ? e.height <= maxHeight : e.height >= maxHeight;
		 
		if (test ) {
			
			e.setAttribute("animating", "false");
			return null;
		}
		e.setAttribute("animating", "true");
		return newVal;
	};
	
	animateObjAttribute(icon, "height", id, modifyOperation, finalHandler);
}

function animateObjAttribute(element, attribute, id, modifyOperation, finalHandler) {

	const attributeVal = element.getAttribute(attribute);
	var newValue = modifyOperation(element, attributeVal); //modifyOperation returns null if will end timeout, otherwise returns result value of the attribute

	if (newValue == null) {
		clearTimeout(timeOuts[id]);
		if (finalHandler) {
			finalHandler();
		}
	} else {
		element.setAttribute(attribute, newValue);

		setTimeout(
				function() {
					animateObjAttribute(element, attribute, id, modifyOperation,
							finalHandler);
				}, 1);
	}
}