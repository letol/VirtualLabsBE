package it.polito.ai.es2.services;

public class StudentAlreadyMemberForCourseException extends TeamServiceException {
    public StudentAlreadyMemberForCourseException() {
        super();
    }

    public StudentAlreadyMemberForCourseException(String message) {
        super(message);
    }
}
