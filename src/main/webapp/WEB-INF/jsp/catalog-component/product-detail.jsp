<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<div id="detail-content" class="row"
	style="width: 95%; margin: auto; display: none">
	<table class="table" style="layout: fixed;">
		<tr>
			<td style="width: 60%">
				<button class="btn btn-primary btn-sm" id="close-detail"
					onclick="closeDetail()">Back</button>
			</td>
			<td style="width: 40%">
				<h2 id="product-title"></h2>

			</td>
		</tr>
		<tr valign="top">
			<td style="width: 60%">

				<div id="carousel-wrapper" style="width: 100%; margin: auto">
					<div id="carouselExampleIndicators" class="carousel slide"
						data-ride="carousel">
						<ol id="carousel-indicators" class="carousel-indicators">
						</ol>
						<div id="carousel-inner" class="carousel-inner"></div>
						<a class="carousel-control-prev" href="#carouselExampleIndicators"
							role="button" data-slide="prev"> <span
							class="carousel-control-prev-icon" aria-hidden="true"></span> <span
							class="sr-only">Previous</span>
						</a> <a class="carousel-control-next"
							href="#carouselExampleIndicators" role="button" data-slide="next">
							<span class="carousel-control-next-icon" aria-hidden="true"></span>
							<span class="sr-only">Next</span>
						</a>
					</div>
				</div>
			</td>
			<!--  -->
			<td style="width: 40%">
				<!-- END CAROUSEL -->

				<ul class="list-group">
					<li
						class="list-group-item d-flex justify-content-between align-items-center">
						Stock <span class="badge badge-primary badge-pill"
						id="product-stock">0</span>
					</li>
					<li
						class="list-group-item d-flex justify-content-between align-items-center">
						Price<br> <span id="product-price">0</span>
					</li>
					<li
						class="list-group-item d-flex justify-content-between align-items-center">
						Unit<br> <span id="product-unit">0</span>
					</li>
					<li
						class="list-group-item d-flex justify-content-between align-items-center">
						Category<br> <span id="product-category">0</span>
					</li>
					<li
						class="list-group-item d-flex justify-content-between align-items-center">
						<button class="btn btn-primary" onclick="showproductsuppliers()">Supplier
							List</button>
					</li>


				</ul>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<p>Description</p>
				<p id="product-description">0</p>
			</td>
		</tr>
	</table>
</div>