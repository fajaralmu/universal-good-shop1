package com.fajar.shoppingmart.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaProducerConfiguration implements DynamicBean {

	private final String bootstrapAddress;
	private final ConfigurableBeanFactory factory;

	public KafkaProducerConfiguration(String bootstrapAddress, ConfigurableBeanFactory factory) {
		this.bootstrapAddress = bootstrapAddress;
		this.factory = factory;
		log.debug("KafkaProducerConfiguration instantiated");
	}

	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();

		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	public KafkaTemplate<String, Object> kafkaTemplate() {
		KafkaTemplate<String, Object> template = new KafkaTemplate<String, Object>(producerFactory());

		return template;
	}

	@Override
	public void registerBean() {
		try {
			factory.registerSingleton("producerFactory", producerFactory());
			factory.registerSingleton("kafkaTemplate", kafkaTemplate());
			log.debug("REGISTERED: Kafka Producer");
		} catch (Exception e) {
			log.error("Error registering kafka producer");
			e.printStackTrace();
		}
	}
}