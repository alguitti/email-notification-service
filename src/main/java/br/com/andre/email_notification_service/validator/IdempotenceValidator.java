package br.com.andre.email_notification_service.validator;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import br.com.andre.core.model.ProductEvent;
import br.com.andre.email_notification_service.dao.ProductEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotenceValidator implements BaseValidator<ProductEvent> {

	private final ProductEventRepository eventRepository;
	
	private static final String MESSAGE = "ID ALREADY EXISTS!";
	
	@Override
	public String specificValidation(ProductEvent event) {
		
		if (eventRepository.findByMessageId(event.getId()).isPresent()) {
			log.error("[Idempotence_Validator] - " + MESSAGE);
			return MESSAGE;
		}
		
		return null;
	}
	
}
