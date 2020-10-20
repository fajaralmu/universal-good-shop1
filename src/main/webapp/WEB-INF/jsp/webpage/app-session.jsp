
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
	<table id="app-sessions">
	</table>
	<p></p>
</div>
<script type="text/javascript">
	var ctxPath = "${contextPath}";

	function deleteSession(requestId) {
		infoLoading();
		var requestObject = {
			'registeredRequest' : {
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

		const header = createTableHeaderByColumns([ "ID", "Referer","IP Address", "Requested Date",
				"User Agent", "Option" ])
		table.appendChild(header);
		const rows = createTableBody([ "requestId", "referrer", "ipAddress", "created", "userAgent" ],
				entities);
		for (var i = 0; i < rows.length; i++) {
			const row = rows[i];
			const entity = entities[i]; 
			const optionCell = createOptionCell(entity, i);
			
			row.appendChild(optionCell);

			const rowMessage = createRow("<td colspan=\"6\" id=\""+entity.requestId+"\"></td>");
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
		})
		 
		
	}

	function updateMessage(response) {
		 
		if (byId(response.code)) {
			byId(response.code).innerHTML = "<button class=\"btn btn-secondary\" id=\"do-toggle-msg"+response.code+"\" >Toggle Chat("
					+ response.entities.length + ")</button>";
			const messages = response.entities;

			for (var i = 0; i < messages.length; i++) {
				const message = messages[i];
				const dateComponent = "<span style=\"font-size:0.7em\">at "
						+ message.date + "</span>";
				if (message.admin == 1) {
					message.text = "ADMIN "
							+ dateComponent
							+ "<div style=\"border-radius:4px; margin:2px;padding:3px;  background-color: cadetblue\">"
							+ message.text + "</div>";
				} else {
					let alias = !message.alias || message.alias.trim() == ""?"" :"["+message.alias+"]";
					message.text = "USER "+alias
							+ dateComponent
							+ "<div style=\"border-radius:4px; margin:2px;padding:3px; background-color: wheat\">"
							+ message.text + "</div>";
				}
			}

			const rows = createTableBody([ "text" ], messages, 0, true);
			const tableMsg = createTableFromRows(rows, "chat-msg-"
					+ response.code);
			const rowReply = createRow("<td colspan=\"2\"><input  class=\"form-control\" type=\"text\" id=\"reply-msg"+response.code+"\" placeholder=\"reply\" />"
					+ "<button class=\"btn btn-success\" id=\"do-reply-msg"+response.code+"\" ><i class=\"fas fa-paper-plane\"></i></button></td>");

			tableMsg.style.tableLayout = "fixed";
			tableMsg.style.width = "100%";
			tableMsg.style.display = "block";
			tableMsg.appendChild(rowReply);
			byId(response.code).appendChild(tableMsg);
			byId("do-reply-msg" + response.code).onclick = function(
					e) {
				const message = byId("reply-msg"+ response.code).value;
				sendReply(response.code, message);
			}

			byId("do-toggle-msg" + response.code).onclick = function(e) {
				var display = "block";
				if (byId("chat-msg-" + response.code).style.display == "block") {
					display = "none";
				}
				byId("chat-msg-" + response.code).style.display = display;
			}

		}
	}

	function connectWesocket() {
		
		addWebsocketRequest('/wsResp/messages', function(
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

