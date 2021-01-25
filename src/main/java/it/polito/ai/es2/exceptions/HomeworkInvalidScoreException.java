package it.polito.ai.es2.exceptions;

public class HomeworkInvalidScoreException extends TeamServiceException {
    public HomeworkInvalidScoreException() {
        super();
    }

    public HomeworkInvalidScoreException(String message) {
        super(message);
    }
}
