package com.fajar.shoppingmart.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.UserRole;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SessionFactoryConfig {
	private static SessionFactory factory; 

	@Autowired 
	private DriverManagerDataSource driverManagerDataSource;
	@Autowired
	private EntityManagerFactory entityManagerFactoryBean;

	@Bean
	@Primary
	public SessionFactory generateSession() {
		
		log.info("=============SESSION FACTORY==========");
		try {
			org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

			configuration.setProperties(additionalProperties());
			
			/**
			 * adding persistence classes
			 */
			configuration.addAnnotatedClass(User.class);
			configuration.addAnnotatedClass(UserRole.class);

			factory = configuration./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory(); 
			log.info("Session Factory has been initialized");
			return factory;
		} catch (Exception ex) {
			
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}

	
	private Properties additionalProperties() {
		
		String dialect = entityManagerFactoryBean.getProperties().get("hibernate.dialect").toString();
		String ddlAuto = entityManagerFactoryBean.getProperties().get("hibernate.hbm2ddl.auto").toString();
		 
		
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", driverManagerDataSource.getUrl());
		properties.setProperty("hibernate.connection.username", driverManagerDataSource.getUsername());
		properties.setProperty("hibernate.connection.password", driverManagerDataSource.getPassword());
		
		properties.setProperty("hibernate.connection.driver_class", com.mysql.jdbc.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);
		return properties;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public Session hibernateSession(SessionFactory sessionFactory) {

		return sessionFactory.openSession();
	}

}
