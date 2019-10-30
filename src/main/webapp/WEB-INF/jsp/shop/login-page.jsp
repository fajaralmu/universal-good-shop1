
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 
<script type="text/javascript">
	var ctxPath = "${contextPath}";
	function login() {

		var username = document.getElementById("useraname").value;
		var password = document.getElementById("password").value;
		var request = new XMLHttpRequest();
		infoLoading();
		var requestObject = {
			'user' : {
				'username' : username,
				'password' : password
			}
		}
		postReq(
				"<spring:url value="/api/account/login" />",
				requestObject,
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						alert("LOGIN SUCCESS");
						window.location.href = "<spring:url value="/admin/home" />";
					} else {
						alert("LOGIN FAILS");
					}
				});
	}

	function goToRegister() {
		window.location.href = "<spring:url value="/account/register" />";
	}
</script>
<div class="content">
		<p id="info" align="center"></p>
		<div class="wrapper-login-form">

			<div class="login-form">

				<span style="font-size: 2em;">Please Login</span> <br> <label
					for="useraname"> Username </label> <br> <input id="useraname"
					class="form-control" type="text" /> <br /> <label for="password">
					Password </label> <br>
				<input id="password" type="password" class="form-control" /> <br />
				<button class="btn btn-primary" onclick="login(); return false;">Login</button>
				<button class="btn btn-success"
					onclick="goToRegister(); return false;">Register</button>

			</div>
		</div>
	</div>
 