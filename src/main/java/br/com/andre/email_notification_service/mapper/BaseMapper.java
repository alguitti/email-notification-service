package br.com.andre.email_notification_service.mapper;

public interface BaseMapper<T,U> {
	U map(T event);
}
