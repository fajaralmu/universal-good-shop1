<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<div class="form">
	<div class="card">
		<div class="card-header"><i class="fa fa-address-card" aria-hidden="true"></i> Customer</div>
		<div class="card-body">
			<div class="dynamic-dropdown-form">
				<input id="input-customer" placeholder="customer name" type="text"
					onkeyup="loadCustomerList()" class="form-control" />
				<input id="input-customer-id" on-enter="loadCustomerListById()" placeholder="customer ID" type="text"
					  class="form-control onenter" /> <select
					 id="customer-dropdown" class="form-control"
					multiple="multiple">
				</select>
			</div>
		</div>
	</div>
	<!-- <p>Customer Detail</p> -->
	<div class="panel">
		<h3 id="customer-name"></h3>
		<p id="customer-address"></p>
		<p id="customer-contact"></p>
	</div>
</div>
<script type="text/javascript">
	const inputCustomerField = byId("input-customer");
	const inputCustomerIdField = byId("input-customer-id");
	const customerListDropDown = byId("customer-dropdown");

	function loadCustomerList() {
		const filterValue = inputCustomerField.value;

		loadStakeHolderList(customerListDropDown, 'customer', 'name', filterValue, handleSelectCustomer);
	}
	
	function loadCustomerListById(){
		const filterValue = inputCustomerIdField.value;
		
		loadStakeHolderListDetailed(customerListDropDown, 'customer', 'id',
				filterValue, handleSelectCustomer, 'name', 0, 10, true);
	}
	
	function handleSelectCustomer(entity){
		inputCustomerField.value = entity.name;
		byId("customer-name").innerHTML = entity.name;
		inputCustomerIdField.value = entity.id;
		/* byId("customer-address").innerHTML = entity.address;
		byId("customer-contact").innerHTML = entity.contact; */
		currentCustomer = entity;
	}
	
	 
</script>