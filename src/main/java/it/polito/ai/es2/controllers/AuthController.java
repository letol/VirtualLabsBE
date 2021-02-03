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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
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
    public boolean createAuthenticationUser(@RequestBody Map<String,String> form) {
        /*{
            "id": String,
            "lastname": String,
            "firstname": String,
            "password": String,
            "email": String
        }*/
        try {
            if (form.containsKey("id") &&
                    form.containsKey("lastName") &&
                    form.containsKey("firstName") &&
                    form.containsKey("password") &&
                    form.containsKey("email")) {

                userManagementService.addUser(form.get("id"),
                        form.get("lastName"),
                        form.get("firstName"),
                        form.get("password"),
                        form.get("email"));

                return true;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Missing form key");
            }
        } catch (EmailNotValidException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
