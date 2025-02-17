package br.com.andre.email_notification_service.dao;

import org.springframework.stereotype.Component;

import br.com.andre.email_notification_service.model.ProductEventEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventPersistenceHandler implements BasePersistenceHandler<ProductEventEntity>{

	private final ProductEventRepository eventRepository;
	
	@Override
	public void saveEvent(ProductEventEntity event) {
		
		eventRepository.save(event);
		
	}

}
