package com.lifeguardian.lifeguardian.services;

import com.lifeguardian.lifeguardian.models.HealthData;
import com.lifeguardian.lifeguardian.models.SensorsData;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

import java.util.Map;

public interface UserService {

    User createUser(User user) throws UserAlreadyExistsException;
    User addUser(User user) throws  UserAlreadyExistsException  ;
    void delete(String email) throws UserNotFoundException;

//    User findBy(String username);

   // void removeToken(String token);

    User getLoggedUser();

    void removeToken(String token);


    User findBy(String username, String password);

    void addPendingDoctor(User user, String doctorUSername);

    JsonObject CalculateHealthStatus(HealthData healthData, SensorsData sensorsData);
}