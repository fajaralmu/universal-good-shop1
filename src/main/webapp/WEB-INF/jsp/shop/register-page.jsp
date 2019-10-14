
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
<title>Register</title>
<link rel="icon" href="<c:url value="/res/img/javaEE.ico"></c:url >"
	type="image/x-icon">
<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/style/style.css?version=1"></c:url> />

<script src="<c:url value="/res/js/ajax.js"></c:url >"></script>
<script src="<c:url value="/res/js/util.js"></c:url >"></script>
<script type="text/javascript">
	function register() {

		var username = document.getElementById("useraname").value;
		var name = document.getElementById("name").value;
		var password = document.getElementById("password").value;
		var request = new XMLHttpRequest();
		infoLoading();
		var requestObject = {
			'user' : {
				'displayName':name,
				'username' : username,
				'password' : password
			}
		}
		postReq(
				"<spring:url value="/api/account/register" />",
				requestObject,
				function(xhr) {
					var response = xhr.data;
					if (response != null && response.code == "00") {
						alert("register SUCCESS");
						window.location.href = "<spring:url value="/account/login" />";
					} else {
						alert("register FAILS");
					}
				} );
	}
</script>
<style>
body {
	background-image: url("<c:url value="/res/img/sea-bg.jpg"></c:url >");
	background-position: center;
	background-size: cover;
	background-repeat: no-repeat;
}

.login-field {
	width: 100%;
	height: 15%;
	border: none;
	color: green;
}

.tombol[name='login'] {
	border: solid 3px orange;
	margin: auto;
	width: 40%;
	padding: 5px;
	margin: auto;
	color: white;
	background-color: orange;
	font-size: 20px;
	transition-duration: 0.4s;
}

.tombol[name='login']:hover {
	color: orange;
	background-color: rgba(255, 255, 255, 0.8);
	cursor: pointer;
}

.wrapper-login-form {
	margin-left: 65%;
	box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0
		rgba(0, 0, 0, 0.19);
	text-align: center;
	margin-right: 10%;
	margin-top: 10%
}
</style>
</head>
<body>
	<div id="loading-div"></div>
	<div class="body">
		<p id="info" align="center"></p>
		<div class="wrapper-login-form">

			<div class="login-form">
			 	<span style="font-size: 2em;">Silakan Register</span> <br> <br>
				<label for="useraname"> Username </label> <br> <br> <input
					id="useraname" class="login-field" type="text" /> <br /> <br />
				<label for="name"> Display Name </label> <br> <br> <input
					id="name" class="login-field" type="text" /> <br /> <br />
				<label for="password"> Kata sandi </label> <br> <br> <input
					id="password" type="password" class="login-field" /> <br /> <br />
				<button class="tombol" name="login" onclick="register(); return false;">Submit</button>
				
			</div>
		</div>
	</div>
</body>
</html>