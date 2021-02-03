package it.polito.ai.es2.exceptions;

public class VmPermissionDenied extends TeamServiceException {
    public VmPermissionDenied() {
        super();
    }

    public VmPermissionDenied(String message) {
        super(message);
    }
}