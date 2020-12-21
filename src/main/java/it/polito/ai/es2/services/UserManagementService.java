package it.polito.ai.es2.services;

import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.dtos.UserDTO;
import it.polito.ai.es2.entities.Teacher;
import it.polito.ai.es2.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserManagementService {

    User addUser(String id, String lastname, String firstname, String password, String email);

    Optional<UserDTO> getUser(Long userId);

    Optional<UserDTO> getUser(String username);

    List<UserDTO> getAllUsers();

    void removeUser(Long userId);

    void removeUser(String username);

    void changePassword(Long userId, String newPassword);

    void changePassword(String username, String newPassword);
}
