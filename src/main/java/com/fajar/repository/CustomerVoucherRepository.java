package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entity.CustomerVoucher; 

public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {  

}
