package it.polito.ai.es2.services;

public class TeacherNotFoundException extends TeamServiceException {
    public TeacherNotFoundException() {
        super();
    }

    public TeacherNotFoundException(String message) {
        super(message);
    }
}
