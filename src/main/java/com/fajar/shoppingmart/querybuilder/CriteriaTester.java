package com.fajar.shoppingmart.querybuilder;

import java.util.Properties;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.Unit;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.UserRole;
import com.fajar.shoppingmart.repository.RepositoryCustomImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CriteriaTester {
	// test
	static Session testSession;

	static ObjectMapper mapper = new ObjectMapper();
	
	public static void main(String[] args) throws Exception { 
		try {
		setSession();
		
		//String filterJSON = "{\"entity\":\"product\",\"filter\":{\"exacts\":false,\"limit\":10,\"page\":0,\"fieldsFilter\":{\"withStock\":false,\"withSupplier\":false,\"withCategories\":false,\"category,id[EXACTS]\":\"4\",\"name\":\"\"},\"orderBy\":null,\"orderType\":null}}";
		String filterJSON = "{\"entity\":\"product\",\"filter\":{\"limit\":5,\"page\":9,\"orderBy\":null,\"orderType\":null,\"fieldsFilter\":{}}}";
		WebRequest request = mapper.readValue(filterJSON, WebRequest.class);
		CriteriaBuilder cb = new CriteriaBuilder(testSession, Product.class, request.getFilter());
		Criteria criteria = cb.createRowCountCriteria();

//		Criteria criteria = cb.createRowCountCriteria();
		 
		criteria.list();
		System.out.println(RepositoryCustomImpl.getWhereQuery(criteria));
		}catch (Exception e) {
			// TODO: handle exception
		}finally {
		System.exit(0);
		}
	}
	
	static void setSession() {

		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

		configuration.setProperties(additionalProperties());
		configuration.addAnnotatedClass(Product.class);
		configuration.addAnnotatedClass(Unit.class);
		configuration.addAnnotatedClass(Category.class);
		configuration.addAnnotatedClass(Transaction.class);
		configuration.addAnnotatedClass(Supplier.class);
		configuration.addAnnotatedClass(Customer.class);
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(UserRole.class);
//		addAnnotatedClass(configuration);

		SessionFactory factory = configuration./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory();
		testSession = factory.openSession();
	}

	private static Properties additionalProperties() {

		String dialect = "org.hibernate.dialect.MySQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/goodshop");
		properties.setProperty("hibernate.connection.username", "root");
		properties.setProperty("hibernate.connection.password", "");

		properties.setProperty("hibernate.connection.driver_class", com.mysql.jdbc.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);

		return properties;
	}
}
