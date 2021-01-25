package it.polito.ai.es2.exceptions;

public class AssignmentInvalidExpiryDateException extends TeamServiceException {
    public AssignmentInvalidExpiryDateException() {
        super();
    }

    public AssignmentInvalidExpiryDateException(String message) {
        super(message);
    }
}
