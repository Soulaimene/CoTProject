package com.lifeguardian.lifeguardian.security;

public class RemoveToken<T> {
    private final T user;

    private final String token;

    public RemoveToken(T user, String token) {
        this.user = user;
        this.token = token;
    }

    public T getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
