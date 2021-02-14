package it.polito.ai.es2.exceptions;

public class EmailNotValidException extends UserManagementServiceException {
    public EmailNotValidException() {
        super("Email is not valid!");
    }

    public EmailNotValidException(String message) {
        super(message);
    }
}
