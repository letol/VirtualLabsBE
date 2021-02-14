package it.polito.ai.es2.exceptions;

public class HomeworkInvalidScoreException extends TeamServiceException {
    public HomeworkInvalidScoreException() {
        super("Invalid score!");
    }

    public HomeworkInvalidScoreException(String message) {
        super(message);
    }
}
