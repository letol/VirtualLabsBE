package it.polito.ai.es2.services;

public class CourseNotEnabledException extends TeamServiceException {
    public CourseNotEnabledException() {
        super();
    }

    public CourseNotEnabledException(String message) {
        super(message);
    }
}
