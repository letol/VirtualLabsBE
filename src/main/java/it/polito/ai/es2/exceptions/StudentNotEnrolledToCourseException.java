package it.polito.ai.es2.exceptions;

public class StudentNotEnrolledToCourseException extends TeamServiceException {
    public StudentNotEnrolledToCourseException() {
        super("Student not enrolled in course!");
    }

    public StudentNotEnrolledToCourseException(String message) {
        super(message);
    }
}
