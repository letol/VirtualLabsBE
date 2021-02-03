package it.polito.ai.es2.controllers;

import it.polito.ai.es2.utility.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserInfo> currentUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        UserInfo userInfo = UserInfo.builder()
                .username(userDetails.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                ).build();
        return ResponseEntity.ok(userInfo);
    }
}
