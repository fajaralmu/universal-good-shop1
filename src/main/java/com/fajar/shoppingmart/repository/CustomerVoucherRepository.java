package com.fajar.shoppingmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.CustomerVoucher;

public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {

}
