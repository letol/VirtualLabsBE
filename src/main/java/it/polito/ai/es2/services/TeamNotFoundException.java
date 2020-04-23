package it.polito.ai.es2.services;

public class TeamNotFoundException extends TeamServiceException {
    public TeamNotFoundException() {
        super();
    }

    public TeamNotFoundException(String message) {
        super(message);
    }
}
