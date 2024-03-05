package com.dan.oauthauthorizationserver.entity;


import com.dan.oauthauthorizationserver.custom.annotations.ValidEmail;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    @ValidEmail
    private String email;
    @Column(length = 60)
    private String password;
    private String role;
    private boolean enabled = false;
}
