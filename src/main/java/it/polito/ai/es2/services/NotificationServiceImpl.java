package it.polito.ai.es2.services;

import it.polito.ai.es2.entities.*;
import it.polito.ai.es2.exceptions.NotificationServiceException;
import it.polito.ai.es2.exceptions.TeamServiceException;
import it.polito.ai.es2.exceptions.TokenNotFoundException;
import it.polito.ai.es2.repositories.ProposalNotificationRepository;
import it.polito.ai.es2.repositories.StudentRepository;
import it.polito.ai.es2.repositories.TeamRepository;
import it.polito.ai.es2.repositories.TokenRepository;
import it.polito.ai.es2.utility.ResponseTypeInvitation;
import it.polito.ai.es2.utility.StudentStatusInvitation;
import it.polito.ai.es2.utility.TeamStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    TokenRepository tokenRepo;

    @Autowired
    TeamService teamService;

    @Autowired
    ProposalNotificationRepository proposalNotificationRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    StudentRepository studentRepository;

    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("***REMOVED***"); //TODO: remove forced address before production
        message.setSubject(subject);
        message.setText(body);
        //TODO remove comment before production
        //mailSender.send(message);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public boolean confirm(String token) throws NotificationServiceException, TeamServiceException {
        Optional<Token> tokenOptional = tokenRepo.findById(token);
        if (!tokenOptional.isPresent())
            throw new TokenNotFoundException("Token '" + token + "' not found!");

        Token t = tokenOptional.get();
        Optional<ProposalNotification> proposalNotification = proposalNotificationRepository.findById(t.getTeamId());
        if(!proposalNotification.isPresent()) throw new TeamServiceException("Proposal Notification Not found");
        if (t.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) return false;
        String studentId = SecurityContextHolder.getContext().getAuthentication().getName();
        Boolean accepted = false;
        Boolean present = false;
        int numAccepted = 0;
        for (StudentStatusInvitation studentStatusInvitation : proposalNotification.get().getStudentsInvitedWithStatus())
        {
            if(studentStatusInvitation.getStudentId().equals(studentId))
            {
                //System.out.println("Aggiorno lo stato dello studente");
                present = true;
                if(studentStatusInvitation.getAccepted()==ResponseTypeInvitation.NOT_REPLY) {
                    studentStatusInvitation.setAccepted(ResponseTypeInvitation.ACCEPTED);
                    accepted = true;
                    numAccepted++;
                } else if (studentStatusInvitation.getAccepted() == ResponseTypeInvitation.ACCEPTED)
                {
                    numAccepted ++;
                }

            }

        }
        if(!present) throw new TeamServiceException("Student not present in proposal");
        if (numAccepted == proposalNotification.get().getStudentsInvitedWithStatus().size() && accepted)
        {
            Course course = proposalNotification.get().getCourse();
            Team newTeam = teamRepository.save(new Team(proposalNotification.get().getTeamName(),course.getVcpu(),course.getMemory(),course.getDisk(),course));
            Set<String> memberIdsSet = new HashSet<>(proposalNotification.get().getStudentsInvitedWithStatus().stream().map(p -> p.getStudentId()).collect(Collectors.toList()));
            memberIdsSet.add(proposalNotification.get().getCreator().getId());
            List<Student> newMembers = studentRepository.findAllById(memberIdsSet);
            newMembers.forEach(newTeam::addMember);
            newTeam.setMaxVmInstance(course.getMaxVmInstance());
            newTeam.setMaxRunningVmInstance(course.getMaxRunningVmInstance());
            newTeam.setStatus(TeamStatus.ACTIVE);
            tokenRepo.delete(t);
            proposalNotificationRepository.delete(proposalNotification.get());


        }


        return accepted;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public boolean reject(String token) throws NotificationServiceException, TeamServiceException {
        /*
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
        */

        Optional<Token> tokenOptional = tokenRepo.findById(token);
        if (!tokenOptional.isPresent())
            throw new TokenNotFoundException("Token '" + token + "' not found!");

        Token t = tokenOptional.get();
        Optional<ProposalNotification> proposalNotification = proposalNotificationRepository.findById(t.getTeamId());
        if(!proposalNotification.isPresent()) throw new TeamServiceException("Proposal Notification Not found");
        if (t.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) return false;
        String studentId = SecurityContextHolder.getContext().getAuthentication().getName();
        Boolean modified = false;
        Boolean present = false;
        for (StudentStatusInvitation studentStatusInvitation : proposalNotification.get().getStudentsInvitedWithStatus())
        {
            if(studentStatusInvitation.getStudentId().equals(studentId))
            {
                System.out.println("Aggiorno lo stato dello studente");
                present = true;
                if(studentStatusInvitation.getAccepted()==ResponseTypeInvitation.NOT_REPLY) {
                    studentStatusInvitation.setAccepted(ResponseTypeInvitation.ACCEPTED);
                    modified = true;
                }
            }
        }

        if(!present) throw new TeamServiceException("Student not present in proposal");

        if (modified)
        {

            tokenRepo.delete(t);
            proposalNotificationRepository.delete(proposalNotification.get());
            return true;
        }


        return false;
    }

    @Override
    public List<String> rejectExpired() {
        List<Token> expiredTokens = tokenRepo.findAllByExpiryDateBefore(
                new Timestamp(System.currentTimeMillis())
        );

        expiredTokens.stream()
                .forEach(tokenRepo::delete);

        List<Long> uniqueIds=expiredTokens.stream()
                .map(Token::getTeamId)
                .distinct()
                .collect(Collectors.toList());
        for (Long id:uniqueIds
             ) {
            proposalNotificationRepository.deleteById(id);
        }

        return expiredTokens.stream()
                .map(Token::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void notifyTeam(ProposalNotification proposalNotification) {
        //long now = System.currentTimeMillis();
        //long duration = 86400000; // 24h
        //Timestamp expiryDate = new Timestamp(now + duration);
        //proposalNotification.setDeadline(new Timestamp(now+proposalNotification.getDeadline().getTime()));
        Token token = new Token(proposalNotification.getToken(), proposalNotification.getId(), proposalNotification.getDeadline());
        proposalNotification.setToken(token.getId());
        tokenRepo.save(token);
        /*memberIds.stream()
                .map(teamService::getStudent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(studentDTO -> {
                    Token t = new Token(UUID.randomUUID().toString(), dto.getId(), expiryDate);
                    tokenRepo.save(t);
                    //TODO uncomment this in production phase
                    //sendMessage(
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
                });*/
    }
}
