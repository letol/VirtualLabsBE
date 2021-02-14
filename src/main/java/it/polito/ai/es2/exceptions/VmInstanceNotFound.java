package it.polito.ai.es2.exceptions;

public class VmInstanceNotFound extends TeamServiceException {
    public VmInstanceNotFound() {
        super("VM instance not found!");
    }

    public VmInstanceNotFound(String message) {
        super(message);
    }
}