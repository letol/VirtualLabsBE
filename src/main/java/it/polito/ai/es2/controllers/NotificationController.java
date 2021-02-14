package it.polito.ai.es2.controllers;

import it.polito.ai.es2.exceptions.TeamServiceException;
import it.polito.ai.es2.services.NotificationService;
import it.polito.ai.es2.exceptions.NotificationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/API/notifications")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{t}")
    public boolean confirmToken(@PathVariable("t") String tokenId) {
        try {
            return notificationService.confirm(tokenId);
        } catch (NotificationServiceException | TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/reject/{t}")
    public boolean rejectToken(@PathVariable("t") String tokenId) {
        try {
            return notificationService.reject(tokenId);
        } catch (NotificationServiceException | TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
