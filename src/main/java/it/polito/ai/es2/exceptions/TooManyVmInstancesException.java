package it.polito.ai.es2.exceptions;

public class TooManyVmInstancesException extends TeamServiceException {
    public TooManyVmInstancesException() {
        super();
    }

    public TooManyVmInstancesException(String message) {
        super(message);
    }
}
