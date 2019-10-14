<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<head>                                                                                                                                                                                                   
</head>
<body onload="disconnect()">
	<p id="info" align="center"></p>
	<div>
		<div>
			<input type="text" id="from" placeholder="Choose a nickname" />
			<button id="btn-join" onclick="join()" >JOIN</button>
		</div>
		<br />
		<div>
			<button id="connect" style="display:none" onclick="connect();">Connect</button>
			<button id="disconnect" style="display:none" disabled="disabled" onclick="disconnect();">
				Disconnect</button>
		</div>
		<br />
		<div id="conversationDiv">
			<input type="text" id="text" placeholder="Write a message..." />
			<button id="sendMessage" onclick="sendMessage();">Send</button>
			<p id="response"></p>
		</div>
	</div>
	
	<script type="text/javascript">
	var joined = false;
	var stompClient = null;
	var user = {};

	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible'
				: 'hidden';
		document.getElementById('response').innerHTML = '';
	}
	
	function connect(){
		doConnect();
		
		
	}
	
	function joinMsg(){
		stompClient.send("/app/addUser", {}, JSON.stringify({
				'user' : {
					'id':user.id*1
				}
			}));
	}
	
	function leaveMsg(){
		stompClient.send("/app/leave", {}, JSON.stringify({
				'user' : {
					'id':user.id*1
				}
			}));
	}

	function doConnect() {
		var socket = new SockJS('/websocket1/chat');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			setConnected(true);
			console.log('Connected -> ' + frame);
			stompClient.subscribe('/wsResp/messages', function(response) {
				var respObject = JSON.parse(response.body);
				console.log("subscribed", respObject);
				showMessageOutput(respObject.message);
			});
			joinMsg();
		});
	}

	function disconnect() {
		if (stompClient != null) {
			leaveMsg();
			stompClient.disconnect();
		}
		setConnected(false);
		console.log("Disconnected");
	}

	function sendMessage() {
		var from = document.getElementById('from').value;
		var text = document.getElementById('text').value;
		stompClient.send("/app/chat", {}, JSON.stringify({
			'from' : from,
			'text' : text
		}));
	}

	function showMessageOutput(message) {
		var response = document.getElementById('response');
		var p = document.createElement('p');
		p.style.wordWrap = 'break-word';
		p.appendChild(document.createTextNode(message.from + ": "
				+ message.text + " (" + message.time + ")"));
		response.appendChild(p);
	}
	
</script>
<script>
	function join(){
		var name = document.getElementById("from").value;
		console.log("WILL JOIN",name);
		
		user.name = name;
		postReq("/websocket/chat-simple/join","name="+name,"join",function(response) {
			var responseObject = JSON.parse(response);
			console.log("RESPONSE", responseObject);
			if(responseObject.responseCode == "00"){
				user.id = responseObject.user.id;
				console.log("USER",user);
				display();
				document.getElementById("from").disabled = true;
			}else{
				alert("FAILED :"+eresponseObject.responseMessage);
			}
					});
		
	}
	
	function display(){
		document.getElementById('connect').style.display = "BLOCK";
		document.getElementById('disconnect').style.display = "BLOCK";
	}
</script>
	
</body>
</html>