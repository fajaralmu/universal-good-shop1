function postReq(url, requestObject, callback) {
	 infoLoading();
	var request = new XMLHttpRequest();
	var param = JSON.stringify(requestObject);
	request.open("POST", url, true);
	request.setRequestHeader("Content-type", "application/json");
	request.onreadystatechange = function() {
		
		if (this.readyState == this.DONE) {
			console.log("RESPONSE ", this.status, this);
			try {
				this['data'] = JSON.parse(this.responseText);
			} catch (e) {
				this['data'] = "{}";
			}
			callback(this);
			infoDone();
		}
		

	}
	request.send(param);
}

function loadEntityList(url, requestObject, callback) {
	postReq(url, requestObject,
			function(xhr) {
				var response = (xhr.data);
				var entities = response.entities;
				if (entities != null && entities[0] != null) {
					callback(entities);

				} else {
					alert("data not found");
				}
			});
}
