package com.fajar.config;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfiguration {
	private static SessionFactory factory;

	@Bean
	public Registry registryBro() throws RemoteException {
		Registry reg;
		try {
			System.out.println("========== REGISTRY CREATING ========= ");
			reg = java.rmi.registry.LocateRegistry.createRegistry(12345);
			System.out.println("========== REGISTRY CREATED ========== ");
			return reg;
		} catch (RemoteException e) {
			e.printStackTrace();
			throw e;
		}

	}

	@Bean
	public SessionFactory generateSession() {
		System.out.println("SESSION FACTORY");
		try {
			factory = new org.hibernate.cfg.Configuration().setInterceptor(new HibernateInterceptor()).configure()
					.buildSessionFactory();
//			Session session = factory.openSession();
			return factory;
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}

	@Bean
	public Session hibernateSession(SessionFactory sessionFactory) {
		return sessionFactory.openSession();
	}

}
