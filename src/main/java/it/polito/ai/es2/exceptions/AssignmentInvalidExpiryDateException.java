package it.polito.ai.es2.exceptions;

public class AssignmentInvalidExpiryDateException extends TeamServiceException {
    public AssignmentInvalidExpiryDateException() {
        super("Invalid expiry date for assignment!");
    }

    public AssignmentInvalidExpiryDateException(String message) {
        super(message);
    }
}
