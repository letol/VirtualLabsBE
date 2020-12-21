package it.polito.ai.es2.exceptions;

public class EmailNotValidException extends UserManagementServiceException {
    public EmailNotValidException() {
        super();
    }

    public EmailNotValidException(String message) {
        super(message);
    }
}
