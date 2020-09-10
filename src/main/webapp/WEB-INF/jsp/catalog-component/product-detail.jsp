<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!-- Detail Product Supplier -->
<div class="modal fade" id="modal-product-suppliers" tabindex="-1"
	role="dialog" aria-labelledby="Product Suppliers" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg"
		role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="title-detail-modal">Supplier</h5>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body"
				style="width: 90%; height: 400px; margin: auto; overflow: scroll;">
				<table class="table" id="table-supplier-list" style="layout: fixed">
				</table>
				<div style="text-align: center">
					<button class="btn btn-outline-success"
						onclick="loadMoreSupplier()">More</button>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>
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