package br.com.andre.email_notification_service.handler;

import java.util.ArrayList;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import br.com.andre.core.model.ProductEvent;
import br.com.andre.email_notification_service.dao.ProductEventRepository;
import br.com.andre.email_notification_service.exception.NotRetryableException;
import br.com.andre.email_notification_service.exception.RetryableException;
import br.com.andre.email_notification_service.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
@KafkaListener(topics = "products-created-events-topic", 
	containerFactory = "pixConcurrentKafkaListenerContainerFactory")
@RequiredArgsConstructor
public class ProductEventHandler {
	
	private final EventService eventService;

	@KafkaHandler
	public void handle(@Payload ProductEvent event,
					   @Header(name = "messageId", required = true) String messageId,
					   @Header(name = KafkaHeaders.RECEIVED_KEY, required = true) String messageKey) {
		
		log.info("[LISTENER] - Event consumed: {}", event);
		log.info("[LISTENER] - Event Headers: {}", messageId);
		log.info("[LISTENER] - Event Key: {}", messageKey);
		
		eventService.execute(event, new ArrayList<String>());

	}

}
