package com.dan.springsecurityclient.controller;

import com.dan.springsecurityclient.entity.User;
import com.dan.springsecurityclient.entity.VerificationToken;
import com.dan.springsecurityclient.event.RegistrationCompleteEvent;
import com.dan.springsecurityclient.model.PasswordModel;
import com.dan.springsecurityclient.model.UserModel;
import com.dan.springsecurityclient.repository.UserRepository;
import com.dan.springsecurityclient.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User verified successfully";
        } else return "Bad user";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationTokenEmail(user, applicationUrl(request), verificationToken);
        return "Verification link sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        String url = "";
        if(user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenEmail(user, applicationUrl(request), token);
        }

        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")) {
            return "Invalid token";
        }

        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password changed";
        }
        else {
            return "Invalid token";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        if(user != null) {
            if(userService.checkIfOldPassValid(user, passwordModel.getOldPassword())) {
                userService.changePassword(user, passwordModel.getNewPassword());
                return "Password saved";
            }
            else return "Invalid pass";
        }
        return "Email not found";
    }

    private String passwordResetTokenEmail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "/savePassword?token=" + token;

        log.info("Click the link reset your password: {}", url);
        return url;
    }

    private void resendVerificationTokenEmail(User user, String applicationUrl, VerificationToken token) {
        String url = applicationUrl + "/verifyRegistration?token=" + token.getToken();

        log.info("Click the link to verify: {}", url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
