package it.polito.ai.es2.components;

import it.polito.ai.es2.services.NotificationService;
import it.polito.ai.es2.services.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    NotificationService notificationService;

    @Autowired
    TeamService teamService;

    @Scheduled(cron = "${cron.token-check}")
    public void tokenCheck() {
        log.info("Token check - Started...");

        notificationService.rejectExpired().forEach(
                tokenId -> log.info("Token check - Expired token deleted (" + tokenId +")")
        );

        log.info("Token check - Finished.");
    }

    @Scheduled(cron = "${cron.assignment-check}")
    public void assignmentCheck() {
        log.info("Assignment check - Started...");

        teamService.submitHomeworksOfExpiredAssignments().forEach(
                assignmentId -> log.info("Assignment check - Auto-submitted homeworks of assignment " + assignmentId)
        );

        log.info("Assignment check - Finished.");
    }
}
