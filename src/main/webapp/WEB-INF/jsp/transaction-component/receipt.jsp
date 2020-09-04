<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<div id="content-receipt" style="display: none">
	<h2>Receipt</h2>
	<table id="table-receipt" style="layout: fixed" class="table">
	</table>
	<button id="btn-close-receipt" class="btn btn-secondary"
		onclick="hide('content-receipt'); show('content-form')">Ok</button>
	<button id="btn-print-receipt" class="btn btn-secondary">Print</button>
</div>