package br.com.andre.email_notification_service.service;

import java.util.List;

import br.com.andre.email_notification_service.dao.BasePersistenceHandler;
import br.com.andre.email_notification_service.mapper.BaseMapper;
import br.com.andre.email_notification_service.rest.RestNotificator;
import br.com.andre.email_notification_service.validator.BaseValidator;

public abstract class BaseServiceImpl<T, U> implements BaseService<T>{

	protected abstract BaseValidator<T> getValidator();
	protected abstract BasePersistenceHandler<U> getBasePersistenceHandler();
	protected abstract BaseMapper<T, U> getBaseMapper();
	protected abstract RestNotificator<T> getNotificator();
	abstract void doInternalValidation(List<String> results);
	
	@Override
	public void execute(T event, List<String> results) {
		
		BaseValidator<T> validator = getValidator();
		validator.validate(event, results);
		
		doInternalValidation(results);
		
		BaseMapper<T, U> mapper = getBaseMapper();
		U entity = mapper.map(event);
		
		BasePersistenceHandler<U> persistenceHandler = getBasePersistenceHandler();
		persistenceHandler.saveEvent(entity);
		
		RestNotificator<T> notificator = getNotificator();
		notificator.dispatch(event);
		
	}
	
}
