package com.lifeguardian.lifeguardian.services;

import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

public interface DoctorService {

    Doctor createDoctor(Doctor doctor) throws UserAlreadyExistsException;
    Doctor addDoctor(Doctor doctor) throws  UserAlreadyExistsException  ;
    void delete(String email) throws UserNotFoundException;


    Doctor getLoggedDoctor();

    void removeToken(String token);

    Doctor findBy(String username, String password);



    void addPendingPatient(Doctor doctor, String patientUsername);

    Response processPendingUser(String doctorUsername, String patientUsername, String status);
}