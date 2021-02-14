package it.polito.ai.es2.exceptions;

public class HomeworkVersionNotFoundException extends TeamServiceException {
    public HomeworkVersionNotFoundException() {
        super("Homework version not found!");
    }

    public HomeworkVersionNotFoundException(String message) {
        super(message);
    }
}
