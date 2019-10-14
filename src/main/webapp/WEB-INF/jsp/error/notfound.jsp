<%@ page language="java" contentType="text/html; charset=windows-1256"
    pageEncoding="windows-1256"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1256">
<title>${pesan }</title>
<style>
body {
background-color: gray;
}
#pesan{
	width:50%;
	height: 30%;
	text-align: center;
	background-color: white;
	color: gray;
	font-family: arial;
	margin: auto;
	opacity:0.6;
}
a{
	text-decoration: none;
}
</style>
</head>
<body>
<div id="pesan"><h1>${pesan }</h1>
<a href="<spring:url value="/user/home" />">home</a>
</div>
</body>
</html>