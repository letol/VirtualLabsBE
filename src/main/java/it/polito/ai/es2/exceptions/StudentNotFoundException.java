package it.polito.ai.es2.exceptions;

import it.polito.ai.es2.exceptions.TeamServiceException;

public class StudentNotFoundException extends TeamServiceException {
    public StudentNotFoundException() {
        super("Student not found!");
    }

    public StudentNotFoundException(String message) {
        super(message);
    }
}
