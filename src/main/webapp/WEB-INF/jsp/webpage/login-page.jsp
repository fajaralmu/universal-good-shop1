
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script type="text/javascript">
	var ctxPath = "${contextPath}";
	function login() {

		var username = byId("user-name").value;
		var password = byId("password").value;
		var request = new XMLHttpRequest();
		loadingButton(); 
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
					loadingButtonDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						infoDialog("LOGIN SUCCESS").then(function(e){
							const redirectLocation = xhr.getResponseHeader("location"); 
							if (redirectLocation != null) {
								window.location.href = redirectLocation;
							} else{
								window.location.href = "<spring:url value="/admin/home" />";
							}
						})
						
					} else {
						alert("LOGIN FAILED");
					}
				});
	}
</script>
<div class="content">
	<p id="info" align="center"></p>
	<form id="login-form">
	<div class="card" style="max-width: 400px; margin: auto">
		<div class="card-header">Please Login</div>
		
		<div class="card-body">
			<div class="login-form">
				<div class="input-group mb-3">
					<div class="input-group-prepend">
						<span class="input-group-text"><i class="fa fa-user-circle"></i></span>
					</div>
					<input placeholder="username" id="user-name" class="form-control" type="text" />
				</div>
				<div class="input-group mb-3">
					<div class="input-group-prepend">
						<span class="input-group-text"> <i class="fa fa-lock"></i></span>
					</div>
					<input placeholder="password" id="password" type="password" class="form-control" />
				</div> 
			</div>
		</div>
		
		<div class="card-footer">
			<input id="btn-login" type="submit" class="btn btn-primary" value="Login"/>
			<a role="button" class="btn btn-success"
				href='<spring:url value="/account/register"></spring:url>'>Register</a>
		</div>
		
	</div>
	</form>
</div>
<script type="text/javascript"> 
	const loginBtn = byId("btn-login");
	
	function loadingButton(){
		loginBtn.innerHTML = "<span class=\"spinner-border spinner-border-sm\" role=\"status\" aria-hidden=\"true\"></span> Logging in";
	}
	function loadingButtonDone(){
		loginBtn.innerHTML = "Login";
	} 
	
	byId("login-form").onsubmit = function(e){
		e.preventDefault();
		login();
	}
</script>
