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
				'lastUpdated': new Date()
			},
			'missiles' : entity.missiles
		}
	}));
}

function connectToWebsocket(callback) {
	var socket = new SockJS('/universal-good-shop/shop-app');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		//setConnected(true);
		console.log('Connected -> ' + frame, stompClient.ws._transport.ws.url);
	 
//		document.getElementById("ws-info").innerHTML = stompClient.ws._transport.ws.url;
		stompClient.subscribe('/wsResp/progress', function(response) {
			console.log("Progress Updated...");
			var respObject = JSON.parse(response.body);
		 	callback(respObject);
		 	//document.getElementById("realtime-info").innerHTML = response.body;
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

function leaveApp(entityId){
	stompClient.send("/app/leave", {}, JSON.stringify({
		'entity' : {
			'id':entityId*1
		}
	}));
}