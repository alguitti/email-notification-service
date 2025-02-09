package br.com.andre.email_notification_service.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import br.com.andre.core.model.ProductEvent;
import lombok.extern.slf4j.Slf4j;

//@KafkaListener(topicPartitions = @TopicPartition(topic = "omin_payments_topic", partitions = {"0"}),
//containerFactory = "pixConcurrentKafkaListenerContainerFactory")

@Slf4j
@Component
@KafkaListener(
		topics = "products-created-events-topic",
		groupId = "products-created-events", 
		containerFactory = "pixConcurrentKafkaListenerContainerFactory")
public class ProductEventHandler{

	@KafkaHandler
	public void handle(ProductEvent event) {
		log.info("[LISTENER] - Event consumed: {}", event);
		System.out.printf("[LISTENER] - Event consumed: %s\n", event.toString());
	}
	
}
