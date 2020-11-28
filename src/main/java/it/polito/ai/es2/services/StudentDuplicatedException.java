package it.polito.ai.es2.services;

public class StudentDuplicatedException extends TeamServiceException {
    public StudentDuplicatedException() {
        super();
    }

    public StudentDuplicatedException(String message) {
        super(message);
    }
}
