<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<div id="progress-bar-wrapper" onclick="hide('progress-bar-wrapper');"
	class="box-shadow"
	style="display: none; z-index:4; height: 50px; padding: 10px; background-color: white; margin: auto; position: fixed; width: 100%">
	<div class="progress">
		<div id="progress-bar"
			class="progress-bar progress-bar-striped bg-info" role="progressbar"
			aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
	</div>
</div>