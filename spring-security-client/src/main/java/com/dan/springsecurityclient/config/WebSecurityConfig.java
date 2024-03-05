package com.dan.springsecurityclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    private static final String[] WHITELIST_URLS = {
            "/hello",
            "/register",
            "/verifyRegistration*",
            "/resendVerifyToken*",
            "/resetPassword",
            "/savePassword",
            "/changePassword"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());
        http.cors((cors) -> cors.disable());
        http.authorizeHttpRequests((auth) -> auth.requestMatchers(WHITELIST_URLS).permitAll());
        http.authorizeHttpRequests((auth) -> auth.requestMatchers("/api/**").authenticated());
        http.oauth2Login(oauth2login -> oauth2login.loginPage("/oauth2/authorization/api-client-oidc")).oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}

