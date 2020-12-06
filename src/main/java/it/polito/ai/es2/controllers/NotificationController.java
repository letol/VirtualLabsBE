package it.polito.ai.es2.controllers;

import it.polito.ai.es2.ViewMessage;
import it.polito.ai.es2.services.NotificationService;
import it.polito.ai.es2.exceptions.NotificationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{t}")
    public String confirmToken(Model model, @PathVariable("t") String tokenId) {
        try {
            notificationService.confirm(tokenId);
            ViewMessage msg = ViewMessage.builder()
                    .title("Participation confirmed")
                    .message("Your participation is now confirmed!")
                    .borderColour("blue")
                    .build();
            model.addAttribute("message", msg);
        } catch (NotificationServiceException nse) {
            ViewMessage msg = ViewMessage.builder()
                    .title("Invalid Token")
                    .message("Ops! Something went wrong!")
                    .borderColour("red")
                    .build();
            model.addAttribute("message", msg);
        }
        return "token";
    }

    @GetMapping("/reject/{t}")
    public String rejectToken(Model model, @PathVariable("t") String tokenId) {
        try {
            notificationService.reject(tokenId);
            ViewMessage msg = ViewMessage.builder()
                    .title("Participation rejected")
                    .message("Your participation is now rejected.")
                    .borderColour("black")
                    .build();
            model.addAttribute("message", msg);
        } catch (NotificationServiceException nse) {
            ViewMessage msg = ViewMessage.builder()
                    .title("Invalid Token")
                    .message("Ops! Something went wrong!")
                    .borderColour("red")
                    .build();
            model.addAttribute("message", msg);
        }
        return "token";
    }
}
