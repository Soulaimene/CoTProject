package com.lifeguardian.lifeguardian.security;

public class UserNotFoundException extends RuntimeException{
    String message ;
    public  UserNotFoundException(String msg){
        super(msg);
        this.message=msg ;
    }

}