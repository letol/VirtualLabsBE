package it.polito.ai.es2.services;

public class TokenNotFoundException extends NotificationServiceException {
    public TokenNotFoundException() {
        super();
    }

    public TokenNotFoundException(String message) {
        super(message);
    }
}
