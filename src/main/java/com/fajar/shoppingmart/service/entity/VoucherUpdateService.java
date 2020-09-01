package com.fajar.shoppingmart.service.entity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.CustomerVoucher;
import com.fajar.shoppingmart.entity.Voucher;
import com.fajar.shoppingmart.repository.CustomerRepository;
import com.fajar.shoppingmart.repository.CustomerVoucherRepository;
import com.fajar.shoppingmart.repository.VoucherRepository;
import com.fajar.shoppingmart.util.ThreadUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoucherUpdateService extends BaseEntityUpdateService<Voucher>{

	@Autowired
	private VoucherRepository voucherRepository; 
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CustomerVoucherRepository CustomerVoucherRepository;
	 
	@Override
	public WebResponse saveEntity(Voucher baseEntity, boolean newRecord) {
		
		Voucher voucher = (Voucher) baseEntity;
		if(newRecord) {
			Voucher dbVoucher = voucherRepository.findTop1ByMonthAndYearAndType(voucher.getMonth(), voucher.getYear(), voucher.getType());
			
			if(dbVoucher != null) {
				newRecord = false;
				voucher.setId(dbVoucher.getId());
			}
		}
		voucher = (Voucher) copyNewElement(baseEntity, newRecord);
		 
		Voucher newVoucher = entityRepository.save(voucher);
		
		if(newRecord) {
			updateCustomerVouchers ( newVoucher);
		}
		
		return WebResponse.builder().entity(newVoucher).build();
	}
	
	private void updateCustomerVouchers(final Voucher newVoucher) {
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				log.info("update customer vouchers");
				
				List<Customer> allCustomer = customerRepository.findAll();
				for (Customer customer : allCustomer) {
					CustomerVoucher customerVoucher = new CustomerVoucher();
					customerVoucher.setVoucher(newVoucher);
					customerVoucher.setMember(customer);
					entityRepository.save(customerVoucher);
				}
			}
		});
	}
}
