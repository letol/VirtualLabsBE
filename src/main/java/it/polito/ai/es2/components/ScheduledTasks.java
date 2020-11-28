package it.polito.ai.es2.components;

import it.polito.ai.es2.services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    NotificationService notificationService;

    @Scheduled(cron = "${cron.token-check}")
    public void tokenCheck() {
        log.info("Token check - Started...");

        notificationService.rejectExpired().forEach(tokenId -> log.info("Token check - Expired token deleted (" + tokenId +")"));

        log.info("Token check - Finished.");
    }
}
