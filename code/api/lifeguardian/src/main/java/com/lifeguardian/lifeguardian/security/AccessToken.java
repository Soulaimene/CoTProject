package com.lifeguardian.lifeguardian.security;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
public class AccessToken {

    @Column
    private String token;

    @Column
    private String jwtSecret;

    @Column
    private LocalDateTime expired;


    @Deprecated
    AccessToken() {
    }

    AccessToken(String token, String jwtSecret, Duration duration) {
        this.token = token;
        this.jwtSecret = jwtSecret;
        this.expired = LocalDateTime.now().plus(duration);
    }

    public String getToken() {
        return token;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public LocalDateTime getExpired() {
        return expired;
    }

    public boolean isValid() {
        final LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expired);
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "token='" + token + '\'' +
                ", jwtSecret='" + jwtSecret + '\'' +
                ", expired=" + expired +
                '}';
    }
}