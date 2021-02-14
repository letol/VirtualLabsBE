package it.polito.ai.es2.exceptions;

public class HomeworkVersionNotInHomeworkException extends TeamServiceException {
    public HomeworkVersionNotInHomeworkException() {
        super("Homework does not have such version!");
    }

    public HomeworkVersionNotInHomeworkException(String message) {
        super(message);
    }
}
