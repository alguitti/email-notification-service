package br.com.andre.email_notification_service.service;

import java.util.List;

public interface BaseService<T> {
	void execute(T event, List<String> results);
}
