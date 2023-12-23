package com.lifeguardian.lifeguardian.security;

import com.lifeguardian.lifeguardian.models.FieldPropertyVisibilityStrategy;
import com.lifeguardian.lifeguardian.models.GrantType;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbVisibility;
import jakarta.validation.constraints.NotBlank;

import java.util.stream.Stream;

@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class Oauth2Request {
    @JsonbProperty("grand_type")
    @NotBlank
    public String grandType;

    @JsonbProperty("username")
    @NotBlank(groups = {GenerateToken.class})
    private String username;
    @JsonbProperty("password")
    @NotBlank(groups = {GenerateToken.class})
    private String password;

    @JsonbProperty("refreshToken")
    @NotBlank(groups = {RefreshToken.class})
    private String refreshToken;


    @JsonbProperty("role")
    private String role;


    public void setGrandType(GrantType grandType) {
        if(grandType != null) {
            this.grandType = grandType.get();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public GrantType getGrandType() {
        if(grandType != null) {
            return GrantType.parse(grandType);
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    public String getRole() {
        return role;
    }

    public @interface  GenerateToken{}

    public @interface  RefreshToken{}
}
