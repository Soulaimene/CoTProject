package com.lifeguardian.lifeguardian.security;

import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class UserJWT {

    private static final Logger LOGGER = Logger.getLogger(UserJWT.class.getName());
    private static final String ISSUER = "jakarta";
    private static final String ROLES = "roles";

    private final String user;
    private final List<String> roles;

    UserJWT(String user, List<String> roles) {
        this.user = user;
        this.roles = roles;
    }

    public String getUser() {
        return user;
    }

    public List<String> getRoles() {
        return roles != null ? roles : Collections.emptyList();
    }
    private static <T> String getUsername(T user) {
        if (user instanceof User) {
            return ((User) user).getUsername();
        } else if (user instanceof Doctor) {
            return ((Doctor) user).getUsername();
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }
    }

    static <T> String createToken(T user, Token token, Duration duration) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(duration);

        try {
            JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
                    .jwtID(getUsername(user)) // You may need to customize this // username , role
                    .issuer(ISSUER)
                    .expirationTime(Date.from(expiresAt))
                    .claim(ROLES, new ArrayList<>(getRoles(user)));

            // Create signed JWT
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).build();
            SignedJWT signedJWT = new SignedJWT(header, claimsSetBuilder.build());
            signedJWT.sign(new MACSigner(token.get()));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            LOGGER.severe("Error creating JWT: " + e.getMessage());
            throw new RuntimeException("Error creating JWT", e);
        }
    }

    private static <T> List<String> getRoles(T user) {
        if (user instanceof User) {
            User castedUser = (User) user;
            return Collections.singletonList(castedUser.getRole());
        } else if (user instanceof Doctor) {
            Doctor castedDoctor = (Doctor) user;
            return Collections.singletonList(castedDoctor.getRole());
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }
    }
}
