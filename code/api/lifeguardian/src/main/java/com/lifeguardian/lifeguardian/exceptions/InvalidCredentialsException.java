package com.lifeguardian.lifeguardian.exceptions;

public class InvalidCredentialsException extends RuntimeException{
    String message ;
    public InvalidCredentialsException(String msg){
        super(msg);
        this.message=msg ;
    }

}