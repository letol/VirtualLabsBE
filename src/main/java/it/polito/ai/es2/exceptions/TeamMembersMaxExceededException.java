package it.polito.ai.es2.exceptions;

public class TeamMembersMaxExceededException extends TeamServiceException {
    public TeamMembersMaxExceededException() {
        super("Exceeded max number of team members!");
    }

    public TeamMembersMaxExceededException(String message) {
        super(message);
    }
}
