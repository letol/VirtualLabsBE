package it.polito.ai.es2.exceptions;

public class TeamNotFoundException extends TeamServiceException {
    public TeamNotFoundException() {
        super();
    }

    public TeamNotFoundException(String message) {
        super(message);
    }
}
