package it.polito.ai.es2.services;

public class TeamMembersMaxExceededException extends TeamServiceException {
    public TeamMembersMaxExceededException() {
        super();
    }

    public TeamMembersMaxExceededException(String message) {
        super(message);
    }
}
