package br.com.andre.email_notification_service.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import br.com.andre.core.model.ProductEvent;
import br.com.andre.email_notification_service.exception.NotRetryableException;
import br.com.andre.email_notification_service.exception.RetryableException;
import lombok.extern.slf4j.Slf4j;

//@KafkaListener(topicPartitions = @TopicPartition(topic = "omin_payments_topic", partitions = {"0"}),
//containerFactory = "pixConcurrentKafkaListenerContainerFactory")

@Slf4j
@Component
@KafkaListener(topics = "products-created-events-topic", groupId = "products-created-events", containerFactory = "pixConcurrentKafkaListenerContainerFactory")
public class ProductEventHandler {

	@Autowired
	private RestTemplate restTemplate;

	private static final String url = "http://localhost:8085";

	@KafkaHandler
	public void handle(ProductEvent event) {
		log.info("[LISTENER] - Event consumed: {}", event);
		System.out.printf("[LISTENER] - Event consumed: %s\n", event.toString());

		try {

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

			if (response.getStatusCode().value() == HttpStatus.OK.value()) {
				log.info("[LISTENER] - Received Response from a remote service: {}", response.getBody());
			}

		} catch (ResourceAccessException e) {
			log.error("[LISTENER] - Error on the http request: {}", e.getMessage());
			throw new RetryableException(e);
		} catch (HttpServerErrorException e) {
			log.error("[LISTENER] - Error on the http request: {}", e.getMessage());
			throw new NotRetryableException(e);
		} catch (Exception e) {
			log.error("[LISTENER] - Error on the http request: {}", e.getMessage());
			throw new NotRetryableException(e);
		}

	}

}
