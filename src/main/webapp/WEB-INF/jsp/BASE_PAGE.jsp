<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1256">
<title>${title}</title>
<link rel="icon" href="<c:url value="/res/img/javaEE.ico"></c:url >"
	type="image/x-icon">

<link rel="stylesheet" type="text/css"
	href="<c:url value="/res/css/shop.css?version=1"></c:url>" />

<link rel="stylesheet" href="<c:url value="/res/css/bootstrap.min.css" />" />
<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js" />" ></script>
<script src="<c:url value="/res/js/popper.min.js" />" ></script>
<script src="<c:url value="/res/js/bootstrap.min.js"  />"></script>
<script src="<c:url value="/res/js/sockjs-0.3.2.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/stomp.js"></c:url >"></script>
<script src="<c:url value="/res/js/websocket-util.js"></c:url >"></script>  
<script src="<c:url value="/res/js/ajax.js?v=1"></c:url >"></script>
<script src="<c:url value="/res/js/util.js?v=1"></c:url >"></script> 
</head>
<body>
	<div id="progress-bar-wrapper" 
	onclick="hide('progress-bar-wrapper');"
	class="box-shadow" 
	style="display:none; height: 50px; padding:10px; background-color: white; margin:auto; position: fixed; width:100%">
			<div class="progress"  >
	 			<div  id="progress-bar" class="progress-bar progress-bar-striped bg-info" role="progressbar"  aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
			</div>
	</div>
	<input id="token-value" value="${pageToken }" type="hidden" />
	<input id="request-id" value="${requestId }" type="hidden" />
	<div id="loading-div"></div>
	<div class="container">
		<jsp:include page="include/head.jsp"></jsp:include>
		<jsp:include page="${pageUrl == null? 'error/notfound': pageUrl}.jsp"></jsp:include>
		<jsp:include page="include/foot.jsp"></jsp:include>
		
	</div>
	<script type="text/javascript">
	function initProgressWebsocket(){
		hide('progress-bar-wrapper');
		var requestId = "${requestId}";
		connectToWebsocket( function(response){
			if(response.requestId.trim() != requestId.trim()){
				return;
			}
			show('progress-bar-wrapper');
			document.getElementById('progress-bar').style.width = response.percentage+"%";
			document.getElementById('progress-bar').setAttribute("aria-valuenow",Math.floor(response.percentage));
			if(response.percentage>=100){
			hide('progress-bar-wrapper');
			}
		},null, null);
	}
	
	document.body.onload = function (){
		console.log("init progress websocket");
		initProgressWebsocket();
		
	}
	</script></body>
</html>