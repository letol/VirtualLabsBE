package it.polito.ai.es2.services;

public interface NotificationService {
    void sendMessage(String address, String subject, String body);
}
