package it.polito.ai.es2.services;

public class NotificationServiceException extends RuntimeException {
    public NotificationServiceException() {
        super();
    }

    public NotificationServiceException(String message) {
        super(message);
    }
}
