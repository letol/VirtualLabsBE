package it.polito.ai.es2.exceptions;

public class CannotDeleteEnabledCourseException extends TeamServiceException {
    public CannotDeleteEnabledCourseException() {
        super("Course is enabled and cannot be deleted!");
    }

    public CannotDeleteEnabledCourseException(String message) {
        super(message);
    }
}
