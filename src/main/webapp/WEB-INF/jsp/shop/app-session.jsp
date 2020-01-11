
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

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
						alert("Operation success!");
						getAppSessions();
					} else {
						alert("Data Not Found");
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
						console.log("Response:", response);
						populateTable(response.entities);
					} else {
						alert("Data Not Found");
					}
				});
	}

	function populateTable(entities) {

		let table = document.getElementById("app-sessions");

		table.innerHTML = "";

		let header = createTableHeaderByColumns([ "ID", "Requested Date",
				"User Agent", "Option" ])
		table.appendChild(header);
		let rows = createTableBody([ "requestId", "created", "userAgent" ],
				entities);
		for (var i = 0; i < rows.length; i++) {
			let row = rows[i];
			let button = createButton("delete-" + i, "Delete");
			button.setAttribute("class", "btn btn-danger");
			const entity = entities[i];
			button.onclick = function(e) {
				if (!confirm("Invalidate Session: " + entity.requestId + "?")) {
					return;
				}
				deleteSession(entity.requestId);
			};
			let optionCell = createCell("");
			row.appendChild(button);

			let rowMessage = createRow("<td colspan=\"5\" id=\""+entity.requestId+"\">Messages</td>");
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
					if (response != null && response.code == "00") {
						console.log("Response:", response);

					} else {
						alert("Data Not Found");
					}
				});
	}

	function updateMessage(response) {
		/* 	console.log("WILL CONSTRUCT MESSAGES: ", response, "COMPONENT:",
					document.getElementById(response.code)); */
		if (document.getElementById(response.code)) {
			document.getElementById(response.code).innerHTML = "<button class=\"btn btn-secondary\" id=\"do-toggle-msg"+response.code+"\" >Toggle Chat("
					+ response.entities.length + ")</button>";
			let messages = response.entities;

			for (var i = 0; i < messages.length; i++) {
				let message = messages[i];
				let dateComponent = "<span style=\"font-size:0.7em\">at "
						+ message.date + "</span>";
				if (message.admin == 1) {
					message.text = "ADMIN "
							+ dateComponent
							+ "<div style=\"border-radius:4px; margin:2px;padding:3px;  background-color: cadetblue\">"
							+ message.text + "</div>";
				} else {
					message.text = "USER "
							+ dateComponent
							+ "<div style=\"border-radius:4px; margin:2px;padding:3px; background-color: wheat\">"
							+ message.text + "</div>";
				}
			}

			let rows = createTableBody([ "text" ], messages,0,true);
			let tableMsg = createTableFromRows(rows, "chat-msg-"
					+ response.code);
			let rowReply = createRow("<td colspan=\"2\"><input  class=\"form-control\" type=\"text\" id=\"reply-msg"+response.code+"\" placeholder=\"reply\" />"
					+ "<button class=\"btn btn-success\" id=\"do-reply-msg"+response.code+"\" >Reply</button></td>");

			tableMsg.style.tableLayout = "fixed";
			tableMsg.style.width = "100%";
			tableMsg.style.display = "block";
			tableMsg.appendChild(rowReply);
			document.getElementById(response.code).appendChild(tableMsg);
			document.getElementById("do-reply-msg" + response.code).onclick = function(
					e) {
				let message = document.getElementById("reply-msg"
						+ response.code).value;
				sendReply(response.code, message);
			}

			document.getElementById("do-toggle-msg" + response.code).onclick = function(
					e) {
				let display = "block";
				if (document.getElementById("chat-msg-" + response.code).style.display == "block") {
					display = "none";
				}
				document.getElementById("chat-msg-" + response.code).style.display = display;
			}

		}
	}

	function connectWesocket() {
		connectToWebsocket(null, function(response) {
			console.log("Response connectWesocket: ", response);
			populateTable(response.entities);

		}, function(response) {
			console.log("Response connectWesocket updateMessage: ", response);
			updateMessage(response);

		});
	}

	getAppSessions();
	connectWesocket();
</script>
<div class="content">
	<p id="info" align="center"></p>
	<h2>App Sessions</h2>
	<table id="app-sessions">
	</table>
	<p></p>
</div>
