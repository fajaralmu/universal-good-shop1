package com.fajar.shoppingmart.repository;

import org.hibernate.Session;

public interface PersistenceOperation {
	
	public void doPersist(Session hibernateSession);

}
