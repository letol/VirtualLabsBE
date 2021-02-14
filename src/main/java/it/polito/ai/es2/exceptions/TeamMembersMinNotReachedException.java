package it.polito.ai.es2.exceptions;

public class TeamMembersMinNotReachedException extends TeamServiceException {
    public TeamMembersMinNotReachedException() {
        super("Not enough members for team!");
    }

    public TeamMembersMinNotReachedException(String message) {
        super(message);
    }
}
