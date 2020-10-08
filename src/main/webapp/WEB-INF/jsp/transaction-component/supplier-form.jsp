<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<div class="form">
	<div class="card">
		<div class="card-header"><i class="fa fa-industry"  ></i> Supplier</div>
		<div class="card-body">
			<div class="dynamic-dropdown-form">
				<input id="input-supplier" placeholder="supplier name" type="text"
					onkeyup="loadSupplierList()" class="form-control" />
				<input id="input-supplier-id" placeholder="supplier ID" type="text"
					on-enter="loadSupplierListById()" class="form-control onenter" /> <select
					 id="supplier-dropdown" class="form-control"
					multiple="multiple">
				</select>
			</div>
		</div>
	</div>
	<!-- <p>Supplier Detail</p> -->
	<div class="panel">
		<h3 id="supplier-name"></h3>
		<p id="supplier-address"></p>
		<p id="supplier-contact"></p>
	</div>
</div>
<script type="text/javascript">
	const inputSupplierField = byId("input-supplier");
	const inputSupplierIdField = byId("input-supplier-id");
	const supplierListDropDown = byId("supplier-dropdown");
	
	function loadSupplierListById(){
		const filterValue = inputSupplierIdField.value;
		
		loadStakeHolderListDetailed(supplierListDropDown, 'supplier', 'id',
				filterValue, handleSelectSupplier, 'name', 0, 10, true);
	}
	
	function loadSupplierList() {

		const filterValue = inputSupplierField.value;

		loadStakeHolderList(supplierListDropDown, 'supplier', 'name',
				filterValue, handleSelectSupplier);
	}
	
	function handleSelectSupplier(entity) {
		 
		inputSupplierField.value = entity.name;
		inputSupplierIdField.value = entity.id;
		byId("supplier-name").innerHTML = entity.name;
		byId("supplier-address").innerHTML = entity.address;
		byId("supplier-contact").innerHTML = entity.contact;
		currentSupplier = entity;
		 
	}
</script>