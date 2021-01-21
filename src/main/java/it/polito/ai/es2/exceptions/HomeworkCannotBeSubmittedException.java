package it.polito.ai.es2.exceptions;

public class HomeworkCannotBeSubmittedException extends TeamServiceException {
    public HomeworkCannotBeSubmittedException() {
        super();
    }

    public HomeworkCannotBeSubmittedException(String message) {
        super(message);
    }
}
