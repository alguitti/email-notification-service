package br.com.andre.email_notification_service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.Message;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.FixedBackOff;

import br.com.andre.core.model.ProductEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

	@Autowired
	private KafkaConfig kafkaConfig;
		
	@Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductEvent> pixConcurrentKafkaListenerContainerFactory() {
		
        ConcurrentKafkaListenerContainerFactory<String, ProductEvent> factory = 
        		new ConcurrentKafkaListenerContainerFactory<>();
        
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
        		new DeadLetterPublishingRecoverer(kafkaTemplate()));
        
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
	
	@Bean
    public ConsumerFactory<String, ProductEvent> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfig());
	}
	
	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
	
	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}
	
	
	@Bean
	public DefaultErrorHandler consumerErrorHandler() {
		ConsumerRecordRecoverer recoverer = new ConsumerRecordRecoverer() {
			@Override
			public void accept(ConsumerRecord<?, ?> t, Exception u) {
				log.error("[ERROR HANDLING] - Tópico: {}, Chave: {}, Offset: {}, Partition: {}", 
						t.topic(), t.key(), t.offset(), t.partition());				
			}
		};			
		return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0L));
	}
	
	public Map<String, Object> consumerConfig() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getServer());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId());
        //ErrorHandlingDeserializer works as a wrapper around deserialization 
        //to catch any exceptions and come around gracefully 
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, kafkaConfig.getTrustedPackages());
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ProductEvent.class);
		return config;
	}
	
	public Map<String, Object> producerConfigs() {
    	Map<String, Object> configs = new HashMap<>();
    	configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getServer());
    	configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    	configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);	
    	return configs;
    }

}
