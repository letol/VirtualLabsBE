package it.polito.ai.es2.exceptions;

public class StudentDuplicatedException extends TeamServiceException {
    public StudentDuplicatedException() {
        super("Duplicated student!");
    }

    public StudentDuplicatedException(String message) {
        super(message);
    }
}
