package com.lifeguardian.lifeguardian.services;

import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;

public interface UserService {

    User createUser(User user) throws UserAlreadyExistsException;
    User addUser(User user) throws  UserAlreadyExistsException  ;
    void delete(String email) throws UserNotFoundException;

//    User findBy(String username);

   // void removeToken(String token);

    User getLoggedUser();

    void removeToken(String token);

    User findBy(String username, String password);
}