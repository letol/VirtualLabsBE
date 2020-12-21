package it.polito.ai.es2.exceptions;

public class UserManagementServiceException extends RuntimeException {
    public UserManagementServiceException() {
        super();
    }

    public UserManagementServiceException(String message) {
        super(message);
    }
}
