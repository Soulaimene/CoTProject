package com.lifeguardian.lifeguardian.security;

import com.lifeguardian.lifeguardian.models.FieldPropertyVisibilityStrategy;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbVisibility;

@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class Oauth2Response {

    @JsonbProperty("access_token")
    private String accessToken;

    @JsonbProperty("username")
    private int expiresIn;

    @JsonbProperty("refresh_token")
    private String refreshToken;


    public String getAccessToken() {
        return accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    static Oauth2Response of(AccessToken accessToken, RefreshToken refreshToken, int expiresIn) {
        Oauth2Response response = new Oauth2Response();
        response.accessToken = accessToken.getToken();
        response.refreshToken = refreshToken.getToken();
        response.expiresIn = expiresIn;
        return response;
    }

}
