package com.fajar.shoppingmart.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatabaseProcessorNotifier {
	
	private List<DatabaseProcessor> databaseProcessors = new ArrayList<DatabaseProcessor>();
	
	public void register(DatabaseProcessor databaseProcessor) {
		log.debug("register db processor");
		this.databaseProcessors.add(databaseProcessor);
	}
	
	public void refresh() {
		log.info("refreshing db processors count :{}", databaseProcessors.size());
		for (DatabaseProcessor databaseProcessor : databaseProcessors) {
			databaseProcessor.refresh();
		}
	}

}
