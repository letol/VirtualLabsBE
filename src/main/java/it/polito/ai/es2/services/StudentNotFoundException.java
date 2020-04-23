package it.polito.ai.es2.services;

public class StudentNotFoundException extends TeamServiceException {
    public StudentNotFoundException() {
        super();
    }

    public StudentNotFoundException(String message) {
        super(message);
    }
}
