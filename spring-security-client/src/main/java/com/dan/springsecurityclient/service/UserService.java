package com.dan.springsecurityclient.service;

import com.dan.springsecurityclient.entity.User;
import com.dan.springsecurityclient.entity.VerificationToken;
import com.dan.springsecurityclient.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfOldPassValid(User user, String oldPassword);
}
