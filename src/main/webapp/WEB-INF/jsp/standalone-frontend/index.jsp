<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!doctype html>
<html lang="en">

<head>
	<meta charset="utf-8" />
	<link rel="icon" href="<c:url value="/res/react-resources/favicon.ico"  />"/>
	<meta name="viewport" content="width=device-width,initial-scale=1" />
	<meta name="theme-color" content="#000000" />
	<meta name="description" content="${shopProfile.shortDescription }" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/res/css/bootstrap/bootstrap.min.css" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/res/fa/css/all.css" />" />
	
	<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js" />"></script>
	<script src="<c:url value="/res/js/popper.min.js" />"></script>
	<script src="<c:url value="/res/js/bootstrap/bootstrap.min.js"  />"></script>
	
	<link rel="apple-touch-icon" href="<c:url value="/res/react-resources/logo192.png" />" />
	<link rel="manifest" href="<c:url value="/res/react-resources/manifest.json" />" />
	<title>${title }</title>
	<link href="<c:url value="/res/react-resources/static/css/main.0a60a1ce.chunk.css" />" rel="stylesheet">
</head>

<body><noscript>You need to enable JavaScript to run this app.</noscript><input type="hidden" id="rootPath" value="${contextPath}"/>
	<div id="root"></div>
	<script>
		!function(l){function e(e){for(var r,t,n=e[0],o=e[1],u=e[2],f=0,i=[];f<n.length;f++)t=n[f],p[t]&&i.push(p[t][0]),p[t]=0;for(r in o)Object.prototype.hasOwnProperty.call(o,r)&&(l[r]=o[r]);for(s&&s(e);i.length;)i.shift()();return c.push.apply(c,u||[]),a()}function a(){for(var e,r=0;r<c.length;r++){for(var t=c[r],n=!0,o=1;o<t.length;o++){var u=t[o];0!==p[u]&&(n=!1)}n&&(c.splice(r--,1),e=f(f.s=t[0]))}return e}var t={},p={2:0},c=[];function f(e){if(t[e])return t[e].exports;var r=t[e]={i:e,l:!1,exports:{}};return l[e].call(r.exports,r,r.exports,f),r.l=!0,r.exports}f.m=l,f.c=t,f.d=function(e,r,t){f.o(e,r)||Object.defineProperty(e,r,{enumerable:!0,get:t})},f.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},f.t=function(r,e){if(1&e&&(r=f(r)),8&e)return r;if(4&e&&"object"==typeof r&&r&&r.__esModule)return r;var t=Object.create(null);if(f.r(t),Object.defineProperty(t,"default",{enumerable:!0,value:r}),2&e&&"string"!=typeof r)for(var n in r)f.d(t,n,function(e){return r[e]}.bind(null,n));return t},f.n=function(e){var r=e&&e.__esModule?function(){return e.default}:function(){return e};return f.d(r,"a",r),r},f.o=function(e,r){return Object.prototype.hasOwnProperty.call(e,r)},f.p="/";var r=window.webpackJsonp=window.webpackJsonp||[],n=r.push.bind(r);r.push=e,r=r.slice();for(var o=0;o<r.length;o++)e(r[o]);var s=n;a()}([])
	</script>
	<script src="<c:url value="/res/react-resources/static/js/1.74d98062.chunk.js" />"></script>
	<script src="<c:url value="/res/react-resources/static/js/main.7b9f4a2a.chunk.js" />"></script>
</body>

</html>