package br.com.andre.email_notification_service.exception;

public class RetryableException extends RuntimeException {

	private static final long serialVersionUID = 7513910603712809706L;

	public RetryableException(String message) {
		super(message);
	}
	
	public RetryableException(Throwable cause) {
		super(cause);
	}
}
