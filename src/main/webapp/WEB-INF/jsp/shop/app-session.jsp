
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
				'registeredRequest':{
					'requestId':requestId
				}
		};
		postReq(
				"<spring:url value="/api/admin/deletesession" />",
				requestObject,
				function(xhr) {
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
		postReq(
				"<spring:url value="/api/admin/appsessions" />",
				requestObject,
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						console.log("Response:",response);	
						populateTable(response.entities);
					} else {
						alert("Data Not Found");
					}
				});
	}
	
	function populateTable(entities){
		
		let table = document.getElementById("app-sessions");
		
		table.innerHTML = "";
		
		let header = createTableHeaderByColumns(["ID","Requested Date", "Referer", "User Agent", "Option"])
		table.appendChild(header);
		let rows = createTableBody(["requestId", "created", "referer", "userAgent"],entities);
		for (var i = 0; i < rows.length; i++) {
			let row =rows[i];
			let button = createButton("delete-"+i, "Delete");
			button.setAttribute("class","btn btn-danger");
			const entity = entities[i];
			button.onclick = function(e){
				if(!confirm("Invalidate Session: "+entity.requestId+"?" )){ return; }
				deleteSession(entity.requestId);
			};
			let optionCell = createCell("");
			row.appendChild(button);
			 
			table.appendChild(row);
		}
	}
	
	getAppSessions();
	 
</script>
<div class="content">
		<p id="info" align="center"></p>
		 <h2>App Sessions</h2>
		 <table id="app-sessions">
		 </table>
		 <p></p>
	</div>
 