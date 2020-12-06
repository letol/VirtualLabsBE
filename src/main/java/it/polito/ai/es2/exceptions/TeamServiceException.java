package it.polito.ai.es2.exceptions;

public class TeamServiceException extends RuntimeException {
    public TeamServiceException() {
        super();
    }

    public TeamServiceException(String message) {
        super(message);
    }
}
