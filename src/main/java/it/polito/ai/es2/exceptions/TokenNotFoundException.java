package it.polito.ai.es2.exceptions;

public class TokenNotFoundException extends NotificationServiceException {
    public TokenNotFoundException() {
        super("Token not found!");
    }

    public TokenNotFoundException(String message) {
        super(message);
    }
}
