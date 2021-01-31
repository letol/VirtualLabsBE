package it.polito.ai.es2.exceptions;

public class VmModelNotFoundException extends TeamServiceException {
    public VmModelNotFoundException() {
        super();
    }

    public VmModelNotFoundException(String message) {
        super(message);
    }
}