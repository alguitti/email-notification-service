package br.com.andre.email_notification_service.exception;

public class NotRetryableException extends RuntimeException{

	private static final long serialVersionUID = 833821628265165927L;
	
	public NotRetryableException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NotRetryableException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
