package com.example.module1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    private Date createdDate;
    private Date expiryDate;

    public PasswordResetToken(String token, AppUser user, Date expiryDate) {
        this.token = token;
        this.user = user;
        this.createdDate = new Date();
        this.expiryDate = expiryDate;
    }

}
