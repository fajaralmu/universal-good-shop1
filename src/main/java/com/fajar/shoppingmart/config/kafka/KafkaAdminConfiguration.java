package com.fajar.shoppingmart.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.kafka.core.KafkaAdmin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaAdminConfiguration implements DynamicBean {
	private final String bootstrapAddress;
	private final ConfigurableBeanFactory factory; 
	private final String topic;

	public KafkaAdminConfiguration(String bootstrapAddress, ConfigurableBeanFactory factory, String topic) {
		this.bootstrapAddress = bootstrapAddress;
		this.factory = factory;
		this.topic = topic;
		log.debug("KafkaAdminConfiguration instantiated bootstrapAddress: {}", bootstrapAddress);
	}

	@Override
	public void registerBean() {
		 try {
			factory.registerSingleton("kafkaAdmin", kafkaAdmin());
			factory.registerSingleton("newTopic", productTopic());
			log.debug("REGISTERED: KafkaAdmin");
		 }catch (Exception e) {
			 log.error("ERROR Registering KafkaAdmin");
			e.printStackTrace();
		}
	}

	 
	public KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

		return new KafkaAdmin(configs);
	}

	 
	public NewTopic productTopic() {
		return new NewTopic(topic, 1, (short) 1);
	}
}