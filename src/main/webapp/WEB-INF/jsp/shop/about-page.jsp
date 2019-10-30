<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">
	<p></p>
	<div class="card" style="width:90%">
		<div class="card-header">About Us</div>
		<div class="card-body">

			<table class="table">

				<tbody>
					<tr>
						<td>Name</td>
						<td>${shopProfile.name }</td>
					</tr>
					<tr>
						<td>Code</td>
						<td>${shopProfile.martCode }</td>
					</tr>
					<tr>
						<td>About</td>
						<td>${shopProfile.about }</td>
					</tr>
					<tr>
						<td>Address</td>
						<td>${shopProfile.address }</td>
					</tr>
					<tr>
						<td>Contact</td>
						<td>${shopProfile.contact }</td>
					</tr>
					<tr>
						<td>Website</td>
						<td>${shopProfile.website }</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>

