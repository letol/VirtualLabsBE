package it.polito.ai.es2.services;

import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.dtos.TeacherDTO;
import it.polito.ai.es2.dtos.UserDTO;
import it.polito.ai.es2.entities.User;
import it.polito.ai.es2.exceptions.EmailNotValidException;
import it.polito.ai.es2.exceptions.UserManagementServiceException;
import it.polito.ai.es2.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    TeamService teamService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User addUser(String id, String lastname, String firstname, String password, String email, MultipartFile avatar) throws UserManagementServiceException, IOException {
        Pattern pattern = Pattern.compile("[sd][0-9]+@(polito|studenti\\.polito)\\.it");
        Matcher matcher = pattern.matcher(email);

        if (!matcher.find()) throw new EmailNotValidException("Email domain is not valid");

        if (email.startsWith("s")) {
            Optional<StudentDTO> opstudentDTO = teamService.getStudent(id);
            if(opstudentDTO.isPresent()){
                if(opstudentDTO.get().getFirstName().equals(firstname) && opstudentDTO.get().getLastName().equals(lastname)
                && opstudentDTO.get().getEmail().equals(email) && opstudentDTO.get().getId().equals(id)){
                    User user = userRepo.save(User.builder()
                            .username(id)
                            .password(passwordEncoder.encode(password))
                            .roles(Collections.singletonList("ROLE_STUDENT"))
                            .build()
                    );
                    teamService.addAuthToStudent(opstudentDTO.get(), user, avatar);
                    return user;
                } else throw new EmailNotValidException("Some fields are not valid");

            } else {
                User user = userRepo.save(User.builder()
                        .username(id)
                        .password(passwordEncoder.encode(password))
                        .roles(Collections.singletonList("ROLE_STUDENT"))
                        .build()
                );
                StudentDTO studentDTO = new StudentDTO(id, lastname, firstname, email, null);
                teamService.addStudent(studentDTO);
                teamService.addAuthToStudent(studentDTO, user, avatar);
                return user;
            }

        } else if (email.startsWith("d")) {
            User user = userRepo.save(User.builder()
                    .username(id)
                    .password(passwordEncoder.encode(password))
                    .roles(Collections.singletonList("ROLE_TEACHER"))
                    .build()
            );
            TeacherDTO teacherDTO = new TeacherDTO(id, lastname, firstname, email, null);
            teamService.addTeacher(teacherDTO);
            teamService.addAuthToTeacher(teacherDTO, user, avatar);
            return user;
        } else throw new EmailNotValidException("Email id is not valid");
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Optional<UserDTO> getUser(Long userId) {
        return userRepo.findById(userId).map(u -> modelMapper.map(u, UserDTO.class));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Optional<UserDTO> getUser(String username) {
        return userRepo.findByUsername(username).map(u -> modelMapper.map(u, UserDTO.class));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeUser(Long userId) throws UserManagementServiceException {
        userRepo.delete(userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id '" + userId + "' not found!")));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeUser(String username) throws UserManagementServiceException {
        userRepo.delete(userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username '" + username + "' not found!")));
    }

    @Override
    @PreAuthorize("#username == authentication.principal.username")
    public void changePassword(Long userId, String newPassword) throws UserManagementServiceException {
        userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id '" + userId + "' not found!"))
                .setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    @PreAuthorize("#username == authentication.principal.username")
    public void changePassword(String username, String newPassword) throws UserManagementServiceException {
        userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username '" + username + "' not found!"))
                .setPassword(passwordEncoder.encode(newPassword));
    }
}
