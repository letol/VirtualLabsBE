package it.polito.ai.es2.exceptions;

public class TeamMembersMaxExceededException extends TeamServiceException {
    public TeamMembersMaxExceededException() {
        super();
    }

    public TeamMembersMaxExceededException(String message) {
        super(message);
    }
}
