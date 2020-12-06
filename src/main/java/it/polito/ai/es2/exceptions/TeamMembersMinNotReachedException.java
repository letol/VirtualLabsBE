package it.polito.ai.es2.exceptions;

public class TeamMembersMinNotReachedException extends TeamServiceException {
    public TeamMembersMinNotReachedException() {
        super();
    }

    public TeamMembersMinNotReachedException(String message) {
        super(message);
    }
}
