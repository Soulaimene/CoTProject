package com.lifeguardian.lifeguardian.services;

import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.security.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.security.UserNotFoundException;

public interface UserService {

    User createUser(User user) throws UserAlreadyExistsException;
    User addUser(User user) throws  UserAlreadyExistsException  ;
    void delete(String email) throws UserNotFoundException;

}