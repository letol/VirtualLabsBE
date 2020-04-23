package it.polito.ai.es2.services;

public class TeamServiceException extends RuntimeException {
    public TeamServiceException() {
        super();
    }

    public TeamServiceException(String message) {
        super(message);
    }
}
