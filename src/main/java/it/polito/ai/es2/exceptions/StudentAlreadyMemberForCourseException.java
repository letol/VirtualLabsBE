package it.polito.ai.es2.exceptions;

public class StudentAlreadyMemberForCourseException extends TeamServiceException {
    public StudentAlreadyMemberForCourseException() {
        super();
    }

    public StudentAlreadyMemberForCourseException(String message) {
        super(message);
    }
}
