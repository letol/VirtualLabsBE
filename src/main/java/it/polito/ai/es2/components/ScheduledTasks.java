package it.polito.ai.es2.components;

import it.polito.ai.es2.repositories.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    TokenRepository tokenRepo;

    @Scheduled(cron = "${cron.token-check}")
    public void tokenCheck() {
        log.info("Token check - Started...");

        Timestamp now = new Timestamp(System.currentTimeMillis());
        tokenRepo.findAllByExpiryDateBefore(now)
                .stream()
                .forEach(token -> {
                    log.info("Token check - Expired token deleted (" + token.getId() +")");
                    tokenRepo.delete(token);
                });

        log.info("Token check - Finished.");
    }
}
