package br.com.andre.email_notification_service.dao;

public interface BasePersistenceHandler<T>{
	void saveEvent(T event);
}
