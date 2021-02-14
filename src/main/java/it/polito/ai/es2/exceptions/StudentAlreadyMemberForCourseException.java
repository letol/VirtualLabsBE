package it.polito.ai.es2.exceptions;

public class StudentAlreadyMemberForCourseException extends TeamServiceException {
    public StudentAlreadyMemberForCourseException() {
        super("Student is already has a team!");
    }

    public StudentAlreadyMemberForCourseException(String message) {
        super(message);
    }
}
