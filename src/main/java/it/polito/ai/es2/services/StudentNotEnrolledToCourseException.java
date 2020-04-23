package it.polito.ai.es2.services;

public class StudentNotEnrolledToCourseException extends TeamServiceException {
    public StudentNotEnrolledToCourseException() {
        super();
    }

    public StudentNotEnrolledToCourseException(String message) {
        super(message);
    }
}
