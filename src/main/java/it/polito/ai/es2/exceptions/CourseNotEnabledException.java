package it.polito.ai.es2.exceptions;

public class CourseNotEnabledException extends TeamServiceException {
    public CourseNotEnabledException() {
        super("Course not enabled yet!");
    }

    public CourseNotEnabledException(String message) {
        super(message);
    }
}
