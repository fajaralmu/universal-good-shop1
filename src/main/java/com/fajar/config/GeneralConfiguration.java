package com.fajar.config;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfiguration {
	

//	@Bean
	public Registry registryBro() throws RemoteException {
		System.out.println("will creating registry");
		
		/**
		 * check existing registry
		 */
//		try {
//		Registry existing = LocateRegistry.getRegistry(12345);
//		System.out.println("Existing registry: "+existing);
//		if(existing != null) {
//			return existing;
//		}
//		}catch(Exception ex) {
//			
//		}
		
		Registry reg;
		try {
			System.out.println("========== REGISTRY CREATING ========= ");
			reg = java.rmi.registry.LocateRegistry.createRegistry(12345);
			System.out.println("========== REGISTRY CREATED ========== ");
			return reg;
		} catch (Exception e) {
			System.out.println("========== REGISTRY ERROR WHEN CREATED ==========");
			e.printStackTrace();
			throw e;
		}

	}

//	@Bean
//	public SessionFactory generateSession() {
//		System.out.println("SESSION FACTORY");
//		try {
//			factory = new org.hibernate.cfg.Configuration().setInterceptor(new HibernateInterceptor()).configure()
//					.buildSessionFactory();
////			Session session = factory.openSession();
//			System.out.println("session factory created");
//			return factory;
//		} catch (Throwable ex) {
//			System.err.println("Failed to create sessionFactory object." + ex);
//			throw new ExceptionInInitializerError(ex);
//		}
//		
//
//	}

//	@Bean
//	public Session hibernateSession(SessionFactory sessionFactory) {
//		return sessionFactory.openSession();
//	}

}
