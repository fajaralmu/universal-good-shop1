
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 
<div class="content">
	<p id="info" align="center"></p>
	<h2>Sessions Management<small> [${registeredRequestId }]</small></h2>
	<button id="btn-clear-sessions" onclick="clearSessions()" class="btn btn-danger">Clear
		All Sessions</button>
	<div style="width:100%; overflow: scroll;">
		<table class="table" id="app-sessions"></table>
	</div>
	<p></p>
</div>
<script type="text/javascript">
	var ctxPath = "${contextPath}";
	const TABLE_HEADERS = [ "ID", "Referer","IP Address", "Requested Date", "User Agent", "Option" ];

	function deleteSession(requestId) {
		infoLoading();
		var requestObject = {
			'registeredrequest' : {
				'requestId' : requestId
			}
		};
		postReq("<spring:url value="/api/admin/deletesession" />",
				requestObject, function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						getAppSessions();
					} else {
						alert("Error delete session");
					}
				});
	}

	function getAppSessions() {

		infoLoading();
		var requestObject = {};
		postReq("<spring:url value="/api/admin/appsessions" />", requestObject,
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						populateTable(response.entities);
					} else {
						alert("Data Not Found");
					}
				});
	}

	function populateTable(entities) {

		const table = byId("app-sessions");

		table.innerHTML = "";

		const header = createTableHeaderByColumns(TABLE_HEADERS)
		table.appendChild(header);
		const rows = createTableBody([ "requestId", "referrer", "ipAddress", "created", "userAgent" ],
				entities);
		for (var i = 0; i < rows.length; i++) {
			const row = rows[i];
			const entity = entities[i]; 
			const optionCell = createOptionCell(entity, i);
			
			row.appendChild(optionCell);

			const rowMessage = createRow("<td colspan=\"7\" id=\"chat-item-"+entity.requestId+"\"></td>");
			table.appendChild(row);
			table.appendChild(rowMessage);
			console.log("=>=>=>messages:", entity.messages);
			
			if (entity.messages != null) {
				console.log("MESSAGES NOT NULL");
				updateMessage({
					'code' : entity.requestId,
					'entities' : entity.messages
				});
			}
		}
	}
	
	function createOptionCell(entity, i){
		const optionCell = createCell("");
		const button = createButton("delete-" + i, "<i class=\"fas fa-trash\"></i>");
		button.setAttribute("class", "btn btn-danger btn-sm");
		button.onclick = function(e) {
			confirmDialog("Invalidate Session: " + entity.requestId + "?")
				.then(function(confirmed){
				if(confirmed){
					deleteSession(entity.requestId);
				}
			});
		}; 
		
		optionCell.appendChild(button);
		
		/* if(entity.requestId != "${registeredRequestId}"){
			const linkVidCall = createAnchor("link-" + i, "VidCall", "<spring:url value="/stream/videocall" />/"+ entity.requestId);
			linkVidCall.setAttribute("class", "button button-info");
			optionCell.appendChild(linkVidCall);
		} */
		
		return optionCell;
	}

	function sendReply(destination, message) {
		infoLoading();
		var requestObject = {
			'destination' : destination,
			'value' : message
		};
		postReq("<spring:url value="/api/admin/replymessage" />",
				requestObject, function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code != "00") {
						alert("Error sending reply");
					}
				});
	}

	function clearSessions() {
		confirmDialog("Are you sure to clear ALL SESSIONS?").then(function(confirmed){
			if(confirmed){
				infoLoading();
				var requestObject = {};
				postReq("<spring:url value="/api/admin/clearsession" />",
						requestObject, function(xhr) {
							infoDone();
							var response = (xhr.data);
							if (response != null && response.code != "00") {
								alert("Error clear session");
							}
						});
			}
		});
	} 
	
	function updateMessage(response) {
		 
		const chatSection = byId("chat-item-"+response.code);
		if (chatSection) {
			
			const buttonToggleChat = createToggleChatButton(response.code, response.entities.length);
			
			chatSection.innerHTML = "";
			chatSection.appendChild(buttonToggleChat);
			const messages = response.entities;
			const messageContent =  createHtmlTag({
				tagName: 'div',
				id: "chat-msg-"+ response.code,
				style: {display:'block', width:'60%'}
			});
			
			for (var i = 0; i < messages.length; i++) {
				const message = messages[i];
				
				const messageItem = createMessageHtmlContent(message);
				messageContent.appendChild(messageItem);
				 
			}
			
			messageContent.appendChild(createInputMessageHtml(response.code));
			chatSection.appendChild(messageContent); 

		}
	}

	function connectWesocket() {
		
		addWebsocketRequest('/wsResp/adminmessages', function(
				response) {
			console.log("Response connectWesocket updateMessage: ", response);
			updateMessage(response);
		});
		addWebsocketRequest('/wsResp/sessions', function(
				response) {
			console.log("Response connectWesocket updateMessage: ", response);
			populateTable(response.entities);
		});
		 
	}

	getAppSessions();
	connectWesocket();
</script>

