package it.polito.ai.es2.components;

import it.polito.ai.es2.entities.User;
import it.polito.ai.es2.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collections;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String admin_user = "admin";
        String admin_pwd = "admin";
        if (!userRepo.findByUsername(admin_user).isPresent()) {
            User admin = User.builder()
                    .username(admin_user)
                    .password(passwordEncoder.encode(admin_pwd))
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();

            userRepo.save(admin);
            log.info("Default administration account created: Username='" + admin_user + "' Password='" + admin_pwd + "'");
        }
    }
}
