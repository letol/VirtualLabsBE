package it.polito.ai.es2.filters;

public class InvalidJwtAuthenticationException extends RuntimeException {
    public InvalidJwtAuthenticationException() {
        super();
    }

    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
