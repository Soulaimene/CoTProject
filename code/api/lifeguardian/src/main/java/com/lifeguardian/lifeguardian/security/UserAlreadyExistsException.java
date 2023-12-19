package com.lifeguardian.lifeguardian.security;

public class UserAlreadyExistsException extends  RuntimeException{
    String message ;
    public UserAlreadyExistsException(String msg) {
        super(msg);
        this.message=msg ;
    }

}