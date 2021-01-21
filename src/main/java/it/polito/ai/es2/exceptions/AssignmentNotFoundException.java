package it.polito.ai.es2.exceptions;

public class AssignmentNotFoundException extends TeamServiceException {
    public AssignmentNotFoundException() {
        super();
    }

    public AssignmentNotFoundException(String message) {
        super(message);
    }
}
