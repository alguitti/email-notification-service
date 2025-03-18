package br.com.andre.email_notification_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.client.RestTemplate;

import br.com.andre.core.model.ProductEvent;
import br.com.andre.email_notification_service.dao.ProductEventRepository;
import br.com.andre.email_notification_service.handler.ProductEventHandler;

@EmbeddedKafka
@SpringBootTest
@ActiveProfiles("test")
public class HandlerIntegrationTest {

	private static final String TOPIC = "products-created-events-topic";
	private static final String URI = "http://localhost:8082/emailFeedback/";


	@Mock
	private ProductEventRepository mockEventRepository;
	
	@Mock
	private RestTemplate mockRestTemplate;
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@MockitoSpyBean
	private ProductEventHandler eventHandler;
	
	
	@Test
	void testHandler_OnProductCreated_HandlesEvent() throws Exception {
		
		when(mockEventRepository.findByMessageId(Mockito.anyString())).thenReturn(Optional.empty());
		when(mockRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), eq(String.class))).thenReturn(new ResponseEntity<String>("any", HttpStatus.OK));
		
		String messageId = UUID.randomUUID().toString();
		ProducerRecord<String, Object> recordEvent = buildEvent(messageId);
		ProductEvent event = (ProductEvent) recordEvent.value();
		kafkaTemplate.send(recordEvent).get();

		ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> messageKeyCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);
		
		verify(eventHandler, timeout(5000).times(1)).handle(eventCaptor.capture(), messageIdCaptor.capture(), messageKeyCaptor.capture());

		assertEquals(messageId, messageIdCaptor.getValue());
		assertEquals(event.getId(), messageKeyCaptor.getValue());
		assertEquals(event, eventCaptor.getValue());
		
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
