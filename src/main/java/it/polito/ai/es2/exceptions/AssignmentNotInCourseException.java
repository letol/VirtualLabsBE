package it.polito.ai.es2.exceptions;

public class AssignmentNotInCourseException extends TeamServiceException {
    public AssignmentNotInCourseException() {
        super();
    }

    public AssignmentNotInCourseException(String message) {
        super(message);
    }
}
