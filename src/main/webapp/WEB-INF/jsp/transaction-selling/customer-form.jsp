<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<div class="form">
	<p>Customer Name</p>
	<input id="input-customer" type="text" onkeyup="loadCustomerList()"
		class="form-control" /> <br /> <select style="width: 200px"
		id="customer-dropdown" class="form-control" multiple="multiple">
	</select>
	<hr>
	<p>Customer Detail</p>
	<div class="panel">
		<h3 id="customer-name"></h3>
		<p id="customer-address"></p>
		<p id="customer-contact"></p>
	</div>
</div>
<script type="text/javascript">
	const inputCustomerField = byId("input-customer");
	const customerListDropDown = byId("customer-dropdown");

	function loadCustomerList() {
		const filterValue = inputCustomerField.value;

		loadStakeHolderList(customerListDropDown, 'customer', 'name',
				filterValue, function(entity) {
					inputCustomerField.value = entity.name;
					byId("customer-name").innerHTML = entity.name;
					/* byId("customer-address").innerHTML = entity.address;
					byId("customer-contact").innerHTML = entity.contact; */
					currentCustomer = entity;
				});
	}
</script>