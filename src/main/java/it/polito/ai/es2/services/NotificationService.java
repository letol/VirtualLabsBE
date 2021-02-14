package it.polito.ai.es2.services;

import it.polito.ai.es2.entities.ProposalNotification;
import it.polito.ai.es2.exceptions.NotificationServiceException;
import it.polito.ai.es2.exceptions.TeamServiceException;

import java.util.List;

public interface NotificationService {

    void sendMessage(String address, String subject, String body);

    boolean confirm(String token) throws NotificationServiceException, TeamServiceException;

    boolean reject(String token) throws NotificationServiceException, TeamServiceException;

    List<String> rejectExpired();

    void notifyTeam(ProposalNotification proposalNotification);
}
