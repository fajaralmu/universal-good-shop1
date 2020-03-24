package com.fajar.service.entity;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Supplier;
import com.fajar.repository.SupplierRepository;

@Service
public class SupplierUpdateService extends BaseEntityUpdateService{

	@Autowired
	private SupplierRepository supplierRepository;
	
	@Override
	public ShopApiResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		Supplier supplier = (Supplier) copyNewElement(baseEntity, newRecord);
		String base64Image = supplier.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage("SPLY", base64Image);
				supplier.setIconUrl(imageName);
			} catch (IOException e) {

				supplier.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Supplier> dbSupplier = supplierRepository.findById(supplier.getId());
				if (dbSupplier.isPresent()) {
					supplier.setIconUrl(dbSupplier.get().getIconUrl());
				}
			}
		}
		Supplier newSupplier = supplierRepository.save(supplier);
		return ShopApiResponse.builder().entity(newSupplier).build();
	}
}
