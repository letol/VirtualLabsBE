package it.polito.ai.es2.exceptions;

public class HomeworkVersionNotInHomeworkException extends TeamServiceException {
    public HomeworkVersionNotInHomeworkException() {
        super();
    }

    public HomeworkVersionNotInHomeworkException(String message) {
        super(message);
    }
}
