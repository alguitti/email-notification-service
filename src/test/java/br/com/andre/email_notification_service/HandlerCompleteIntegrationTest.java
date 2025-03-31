package br.com.andre.email_notification_service;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import br.com.andre.core.model.ProductEvent;
import br.com.andre.email_notification_service.dao.ProductEventRepository;
import br.com.andre.email_notification_service.model.ProductEventEntity;
import br.com.andre.email_notification_service.rest.EventRestNotificator;
import br.com.andre.email_notification_service.service.EventService;

@EmbeddedKafka
@SpringBootTest
@ActiveProfiles("test")
public class HandlerCompleteIntegrationTest {

	private static final String TOPIC = "products-created-events-topic";
	private static final String URI = "http://localhost:8082/emailFeedback/";


	@Autowired
	private ProductEventRepository eventRepository;
	
	@Mock
	private RestTemplate mockRestTemplate;
	
	@MockitoBean
	private EventRestNotificator mockEventRestNotificator;
	
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@BeforeEach
	void setUp() {
		eventRepository.deleteAll();
	}

	@Test
	void testHandler_OnProductCreated_HandlesEvent() throws Exception {
		
//		when(mockRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), eq(String.class))).thenReturn(new ResponseEntity<String>("any", HttpStatus.OK));
		
		doNothing().when(mockEventRestNotificator).dispatch(any());
		
		String messageId = UUID.randomUUID().toString();
		ProducerRecord<String, Object> recordEvent = buildEvent(messageId);
		ProductEvent event = (ProductEvent) recordEvent.value();
		
		CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(recordEvent);
		
		SendResult<String, Object> result = future.join();
		
//		verify(mockRestTemplate, times(1)).exchange(URI + event.getName(), HttpMethod.GET, null, String.class);
	

		Thread.sleep(1000L);
		
		verify(mockEventRestNotificator).dispatch(event);
		List<ProductEventEntity> list = eventRepository.findAll();
		assertFalse(list.isEmpty());
		assertEquals(recordEvent, result.getProducerRecord());
		
	}

	private ProducerRecord<String, Object> buildEvent(String messageId) {
		
		ProductEvent event = new ProductEvent();
		event.setId(UUID.randomUUID().toString());
		event.setName("test");
		event.setDescription("desc");
		
		ProducerRecord<String, Object> record = new ProducerRecord<String, Object>(TOPIC, event.getId(), event);
		record.headers().add("messageId", messageId.getBytes());
		record.headers().add(KafkaHeaders.RECEIVED_KEY, event.getId().getBytes());
		
		return record;
	}
}
