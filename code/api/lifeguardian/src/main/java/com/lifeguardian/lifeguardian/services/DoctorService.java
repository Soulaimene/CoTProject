package com.lifeguardian.lifeguardian.services;

import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;

public interface DoctorService {

    Doctor createDoctor(Doctor doctor) throws UserAlreadyExistsException;
    Doctor addDoctor(Doctor doctor) throws  UserAlreadyExistsException  ;
    void delete(String email) throws UserNotFoundException;

    Doctor findBy(String username);

    Doctor getLoggedDoctor();

    void removeToken(String token);

    Doctor findBy(String username, String password);
}