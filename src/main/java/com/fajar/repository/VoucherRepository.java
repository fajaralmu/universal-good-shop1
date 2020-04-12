package com.fajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.dto.VoucherType;
import com.fajar.entity.Voucher; 

public interface VoucherRepository extends JpaRepository<Voucher, Long> { 
	
	public Voucher findTop1ByMonthAndYear(int month, int year);

	public Voucher findTop1ByMonthAndYearAndType(int month, int year, VoucherType type);

}
