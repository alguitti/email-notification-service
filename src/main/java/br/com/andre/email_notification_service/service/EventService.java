package br.com.andre.email_notification_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import br.com.andre.core.model.ProductEvent;
import br.com.andre.email_notification_service.dao.BasePersistenceHandler;
import br.com.andre.email_notification_service.dao.EventPersistenceHandler;
import br.com.andre.email_notification_service.exception.NotRetryableException;
import br.com.andre.email_notification_service.mapper.BaseMapper;
import br.com.andre.email_notification_service.mapper.EventMapper;
import br.com.andre.email_notification_service.model.ProductEventEntity;
import br.com.andre.email_notification_service.rest.EventRestNotificator;
import br.com.andre.email_notification_service.rest.RestNotificator;
import br.com.andre.email_notification_service.validator.BaseValidator;
import br.com.andre.email_notification_service.validator.IdempotenceValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService extends BaseServiceImpl<ProductEvent, ProductEventEntity> {

	private final EventMapper mapper;
	private final EventPersistenceHandler persistenceHandler;
	private final IdempotenceValidator validator;
	private final EventRestNotificator notificator;

	@Override
	protected BaseValidator<ProductEvent> getValidator() {
		return validator;
	}

	@Override
	protected BasePersistenceHandler<ProductEventEntity> getBasePersistenceHandler() {
		return persistenceHandler;
	}

	@Override
	protected BaseMapper<ProductEvent, ProductEventEntity> getBaseMapper() {
		return mapper;
	}

	@Override
	protected RestNotificator<ProductEvent> getNotificator() {
		return notificator;
	}

	private String toMessage(List<String> results) {
		StringBuilder builder = new StringBuilder();
		results.forEach(e -> {
			builder.append(e);
			builder.append(", ");
		});
		return builder.toString();
	}

	@Override
	public void doInternalValidation(List<String> results) {

		if (!results.isEmpty()) {
			throw new NotRetryableException(toMessage(results));
		}
		
	}

}
