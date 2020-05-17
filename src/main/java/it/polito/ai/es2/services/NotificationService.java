package it.polito.ai.es2.services;

import it.polito.ai.es2.dtos.TeamDTO;

import java.util.List;

public interface NotificationService {

    void sendMessage(String address, String subject, String body);

    boolean confirm(String token);

    boolean reject(String token);

    List<String> rejectExpired();

    void notifyTeam(TeamDTO dto, List<String> memberIds);
}
