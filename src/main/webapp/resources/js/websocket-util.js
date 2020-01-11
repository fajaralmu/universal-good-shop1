var stompClient = null;

function updateMovement() {
	stompClient.send("/app/move", {}, JSON.stringify({
		'entity' : {
			'id' : entity.id * 1,
			'life' : entity.life,
			'active' : true,
			'physical' : {
				'x' : entity.physical.x,
				'y' : entity.physical.y,
				'direction' : entity.physical.direction,
				'color' : entity.physical.color,
				'lastUpdated' : new Date()
			},
			'missiles' : entity.missiles
		}
	}));
}

function connectToWebsocket(callback1, callback2, callback3) {
	let stompClients;
	 
	var socket = new SockJS('/universal-good-shop/shop-app');
	stompClients = Stomp.over(socket);
	stompClients.connect({}, function(frame) {
		// setConnected(true);
		console.log('Connected -> ' + frame, stompClients.ws._transport.ws.url);

		// document.getElementById("ws-info").innerHTML =
		// stompClient.ws._transport.ws.url;

		stompClients.subscribe("/wsResp/progress", function(response) {
			if(!callback1) return;
			console.log("Websocket Updated...");
			var respObject = JSON.parse(response.body);
			callback1(respObject);
			// document.getElementById("realtime-info").innerHTML =
			// response.body;
		});

		stompClients.subscribe("/wsResp/sessions", function(response) {
			if(!callback2) return;
			console.log("Websocket Updated...");
			
			var respObject = JSON.parse(response.body);
			callback2(respObject);
			// document.getElementById("realtime-info").innerHTML =
			// response.body;
		});

		stompClients.subscribe("/wsResp/messages", function(response) {
			if(!callback3) return;
			console.log("Websocket Updated...");
			
			var respObject = JSON.parse(response.body);
			console.log("Response connectWesocket: ", respObject);
			callback3(respObject);
			// document.getElementById("realtime-info").innerHTML =
			// response.body;
		});

	});

}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
}

function leaveApp(entityId) {
	stompClient.send("/app/leave", {}, JSON.stringify({
		'entity' : {
			'id' : entityId * 1
		}
	}));
}