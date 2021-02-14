package it.polito.ai.es2.exceptions;

public class HomeworkNotFoundException extends TeamServiceException {
    public HomeworkNotFoundException() {
        super("Homework not found!");
    }

    public HomeworkNotFoundException(String message) {
        super(message);
    }
}
