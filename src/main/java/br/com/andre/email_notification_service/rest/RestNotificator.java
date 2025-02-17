package br.com.andre.email_notification_service.rest;

public interface RestNotificator<T> {
	
	void dispatch(T event);

}
