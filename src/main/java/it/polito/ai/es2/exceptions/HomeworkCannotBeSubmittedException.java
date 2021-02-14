package it.polito.ai.es2.exceptions;

public class HomeworkCannotBeSubmittedException extends TeamServiceException {
    public HomeworkCannotBeSubmittedException() {
        super("Homework cannot be submitted!");
    }

    public HomeworkCannotBeSubmittedException(String message) {
        super(message);
    }
}
