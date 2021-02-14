package it.polito.ai.es2.services;

import it.polito.ai.es2.dtos.UserDTO;
import it.polito.ai.es2.entities.User;
import it.polito.ai.es2.exceptions.UserManagementServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserManagementService {

    User addUser(String id, String lastname, String firstname, String password, String email, MultipartFile avatar) throws UserManagementServiceException, IOException;

    Optional<UserDTO> getUser(Long userId);

    Optional<UserDTO> getUser(String username);

    List<UserDTO> getAllUsers();

    void removeUser(Long userId) throws UserManagementServiceException;

    void removeUser(String username) throws UserManagementServiceException;

    void changePassword(Long userId, String newPassword) throws UserManagementServiceException;

    void changePassword(String username, String newPassword) throws UserManagementServiceException;
}
