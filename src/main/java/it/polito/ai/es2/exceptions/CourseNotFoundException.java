package it.polito.ai.es2.exceptions;

public class CourseNotFoundException extends TeamServiceException {
    public CourseNotFoundException() {
        super("Course not found!");
    }

    public CourseNotFoundException(String message) {
        super(message);
    }
}
