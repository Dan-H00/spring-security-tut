package com.dan.springsecurityclient.event.listener;

import com.dan.springsecurityclient.entity.User;
import com.dan.springsecurityclient.event.RegistrationCompleteEvent;
import com.dan.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Create the verification token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);
        // Send email to user
        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;

        log.info("Click the link to verify: {}", url);
    }
}
