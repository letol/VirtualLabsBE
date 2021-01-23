package it.polito.ai.es2.exceptions;

public class VmIstanceNotFound extends TeamServiceException {
    public VmIstanceNotFound() {
        super();
    }

    public VmIstanceNotFound(String message) {
        super(message);
    }
}