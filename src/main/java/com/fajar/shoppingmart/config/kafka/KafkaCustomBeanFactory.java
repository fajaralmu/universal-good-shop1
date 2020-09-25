package com.fajar.shoppingmart.config.kafka;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KafkaCustomBeanFactory implements BeanFactoryAware {

	private BeanFactory beanFactory;

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		
	}

	@PostConstruct
	public void onPostConstruct() {
		setKafkaConfiguration();
	}

	private void setKafkaConfiguration() {
		log.debug("setKafkaConfiguration");
		
		ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

		List<DynamicBean> beansToRegister = new ArrayList<>();
		beansToRegister.add(new KafkaAdminConfiguration(bootstrapAddress, configurableBeanFactory));
		beansToRegister.add(new KafkaProducerConfiguration(bootstrapAddress, configurableBeanFactory));
		for (DynamicBean dynamicBean : beansToRegister) {
			dynamicBean.registerBean();
		}
	}

}
