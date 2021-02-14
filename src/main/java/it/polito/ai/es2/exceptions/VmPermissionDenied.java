package it.polito.ai.es2.exceptions;

public class VmPermissionDenied extends TeamServiceException {
    public VmPermissionDenied() {
        super("No such permissions for this VM!");
    }

    public VmPermissionDenied(String message) {
        super(message);
    }
}