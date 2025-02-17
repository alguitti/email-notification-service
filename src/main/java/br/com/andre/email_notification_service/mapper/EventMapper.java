package br.com.andre.email_notification_service.mapper;

import org.springframework.stereotype.Component;

import br.com.andre.core.model.ProductEvent;
import br.com.andre.email_notification_service.model.ProductEventEntity;

@Component
public class EventMapper implements BaseMapper<ProductEvent, ProductEventEntity>{

	@Override
	public ProductEventEntity map(ProductEvent event) {
		
		return ProductEventEntity.builder()
				.messageId(event.getId())
				.productId(event.getName())
				.build();
		
	}

}
