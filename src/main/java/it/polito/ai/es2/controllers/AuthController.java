package it.polito.ai.es2.controllers;

import it.polito.ai.es2.utility.JwtRequest;
import it.polito.ai.es2.utility.JwtResponse;
import it.polito.ai.es2.components.JwtTokenProvider;
import it.polito.ai.es2.exceptions.EmailNotValidException;
import it.polito.ai.es2.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    UserDetailsService userDetailsService;

    @Autowired
    UserManagementService userManagementService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest data) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username,
                    userDetailsService.loadUserByUsername(username).getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList())
            );
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }

    @PostMapping("/register")
    public boolean createAuthenticationUser(@RequestPart("id") String id,
                                            @RequestPart("lastName") String lastname,
                                            @RequestPart("firstName") String firstname,
                                            @RequestPart("password") String password,
                                            @RequestPart("email") String email,
                                            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        try {
            userManagementService.addUser(id,
                    lastname,
                    firstname,
                    password,
                    email,
                    avatar);
            return true;
        } catch (EmailNotValidException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
