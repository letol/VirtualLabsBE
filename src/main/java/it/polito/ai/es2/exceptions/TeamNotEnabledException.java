package it.polito.ai.es2.exceptions;

public class TeamNotEnabledException extends TeamServiceException {
    public TeamNotEnabledException() {
        super();
    }

    public TeamNotEnabledException(String message) {
        super(message);
    }
}