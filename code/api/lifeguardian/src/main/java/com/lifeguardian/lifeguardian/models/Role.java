package com.lifeguardian.lifeguardian.models;


import java.util.function.Supplier;

public enum Role implements Supplier<String> {
    ADMIN, CLIENT;

    @Override
    public String get() {
        return this.name();
    }
}