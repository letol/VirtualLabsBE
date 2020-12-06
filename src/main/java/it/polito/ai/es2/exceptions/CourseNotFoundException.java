package it.polito.ai.es2.exceptions;

public class CourseNotFoundException extends TeamServiceException {
    public CourseNotFoundException() {
        super();
    }

    public CourseNotFoundException(String message) {
        super(message);
    }
}
