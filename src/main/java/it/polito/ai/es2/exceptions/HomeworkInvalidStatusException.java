package it.polito.ai.es2.exceptions;

public class HomeworkInvalidStatusException extends TeamServiceException {
    public HomeworkInvalidStatusException() {
        super();
    }

    public HomeworkInvalidStatusException(String message) {
        super(message);
    }
}
