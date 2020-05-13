package it.polito.ai.es2.services;

import it.polito.ai.es2.dtos.TeamDTO;
import it.polito.ai.es2.entities.Token;
import it.polito.ai.es2.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    TokenRepository tokenRepo;

    @Autowired
    TeamService teamService;

    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("tolomei.leonardo@gmail.com"); //TODO: remove forced address before production
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public boolean confirm(String token) {
        Optional<Token> tokenOptional = tokenRepo.findById(token);
        if (!tokenOptional.isPresent())
            throw new TokenNotFoundException("Token '" + token + "' not found!");

        Token t = tokenOptional.get();
        if (t.getExpiryDate().after(new Timestamp(System.currentTimeMillis()))) {
            tokenRepo.delete(t);
            List<Token> pending = tokenRepo.findAllByTeamId(t.getTeamId());
            if (pending.isEmpty()) {
                teamService.enableTeam(t.getTeamId());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean reject(String token) {
        Optional<Token> tokenOptional = tokenRepo.findById(token);
        if (!tokenOptional.isPresent())
            throw new TokenNotFoundException("Token '" + token + "' not found!");

        Token t = tokenOptional.get();
        if (t.getExpiryDate().after(new Timestamp(System.currentTimeMillis()))) {
            List<Token> pending = tokenRepo.findAllByTeamId(t.getTeamId());
            pending.forEach(i -> tokenRepo.delete(i));
            teamService.evictTeam(t.getTeamId());
            return true;
        }

        return false;
    }

    @Override
    public void notifyTeam(TeamDTO dto, List<String> memberIds) {
        long now = System.currentTimeMillis();
        long duration = Duration.ofHours(1).toMillis();
        Timestamp expiryDate = new Timestamp(now + duration);

        memberIds.stream()
                .map(teamService::getStudent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(studentDTO -> {
                    Token t = new Token(UUID.randomUUID().toString(), dto.getId(), expiryDate);
                    tokenRepo.save(t);
                    sendMessage(
                            "s" + studentDTO.getId() + "@studenti.polito.it",
                            "PolitoAI Lab3 - Email address confirmation",
                            "Dear " + studentDTO.getFirstName() + ",\n" +
                                    "The team " + dto.getName() +
                                    " of course " + teamService.getCourseOfTeam(dto.getId()).getName() +
                                    " was created and you were selected as a member.\n\n" +
                                    "Please click on this link to confirm your participation:\n" +
                                    "http://localhost:8080/notification/confirm/" + t.getId() + "\n\n" +
                                    "If you want to decline your participation or you don't know what this email is, click on this link, instead:\n" +
                                    "http://localhost:8080/notification/reject/" + t.getId() + "\n\n" +
                                    "This email was sent automatically. Please do not reply."
                    );
                });
    }
}
