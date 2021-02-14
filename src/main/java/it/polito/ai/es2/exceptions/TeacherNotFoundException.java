package it.polito.ai.es2.exceptions;

import it.polito.ai.es2.exceptions.TeamServiceException;

public class TeacherNotFoundException extends TeamServiceException {
    public TeacherNotFoundException() {
        super("Teacher not found!");
    }

    public TeacherNotFoundException(String message) {
        super(message);
    }
}
