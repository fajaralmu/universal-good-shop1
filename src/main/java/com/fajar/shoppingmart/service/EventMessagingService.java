package com.fajar.shoppingmart.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.util.ThreadUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventMessagingService {
	@Autowired
	private ApplicationContext applicationContext;
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@PostConstruct
	public void init() {
		checkKafkaTemplate();
	}
	
	private void checkKafkaTemplate() {
	  Object bean = applicationContext.getBean("kafkaTemplate");
	  if(bean != null && bean.getClass().equals(KafkaTemplate.class)) {
		  kafkaTemplate = (KafkaTemplate<String, Object>) bean;
		  log.debug("kafkaTemplate BEAN Found!");
	  }else {
		  log.debug("kafkaTemplate BEAN NOT Found!");
	  }
		
	}

	public void sendEvent(String topic, String key, Object data) {
		 
		if(null != kafkaTemplate) {
			ThreadUtil.run(()->{
				kafkaTemplate.send(topic, key, data);
				log.debug("Event sent to topic: {}", topic);
			});
		}
		
	}

}
