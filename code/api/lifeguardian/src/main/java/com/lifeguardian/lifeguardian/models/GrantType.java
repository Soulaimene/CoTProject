package com.lifeguardian.lifeguardian.models;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum GrantType implements Supplier<String> {

    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token");

    public final String value;

    GrantType(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }

    public static GrantType parse(String value) {
        for (GrantType grantType : GrantType.values()) {
            if (grantType.get().equalsIgnoreCase(value)) {
                return grantType;
            }
        }
        throw new IllegalArgumentException("There is no GrantType " + value);
    }

}