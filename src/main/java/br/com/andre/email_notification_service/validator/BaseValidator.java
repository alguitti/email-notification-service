package br.com.andre.email_notification_service.validator;

import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public interface BaseValidator<T> {
	
	String specificValidation(T event);
	
	default void validate(T event, List<String> results) {
		
		String error = specificValidation(event);
		
		if (error != null) {
			results.add(specificValidation(event));
		}
		
	}

}
