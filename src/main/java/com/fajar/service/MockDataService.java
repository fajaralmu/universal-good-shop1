package com.fajar.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entity.Transaction;
import com.fajar.repository.TransactionRepository;

@Service
public class MockDataService {

	@Autowired
	private TransactionRepository transactionRepository;
	
	static final Random RAND = new Random();

	@PostConstruct
	public void init() {

		List<Transaction> transactions = transactionRepository.findByType("OUT");
		
		System.out.println("******** will begin manipulating , COUNT: "+ transactions.size() +"***********");
		
		Thread thread = new Thread(() -> {
			int i = 0;
			for (Transaction transaction : transactions) {
				Date date = transaction.getTransactionDate();
				
				LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
				localDateTime = localDateTime.plusDays(RAND.nextInt(30)+1); 
				Date newDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
				
				transaction.setTransactionDate(newDate);
				
				transactionRepository.save(transaction);
				i++;
				System.out.println(i+" of "+ transactions.size()+" updated");
			}
		});

		//thread.start();
	}

}