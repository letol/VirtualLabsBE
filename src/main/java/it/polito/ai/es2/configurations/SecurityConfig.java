package it.polito.ai.es2.configurations;

import it.polito.ai.es2.components.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                    .authorizeRequests()
                    .antMatchers("/API/**").authenticated()
                    .anyRequest().permitAll()
                .and()
                    .apply(new JwtConfigurer(jwtTokenProvider));
    }
}
