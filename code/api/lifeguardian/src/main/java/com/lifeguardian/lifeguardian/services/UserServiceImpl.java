package com.lifeguardian.lifeguardian.services;


import com.lifeguardian.lifeguardian.exceptions.UserNotAuthorizedException;
import com.lifeguardian.lifeguardian.models.HealthData;
import com.lifeguardian.lifeguardian.models.SensorsData;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;

import com.lifeguardian.lifeguardian.security.RemoveToken;
import com.lifeguardian.lifeguardian.utils.Argon2Utility;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.core.Response;


import java.security.Principal;
import java.util.stream.Stream;

@ApplicationScoped
public class UserServiceImpl implements UserService {

  @Inject private UserRepository userRepository;
  @Inject Argon2Utility argon2Utility;

  @Inject private SecurityContext securityContext;

  @Inject private Event<RemoveToken<User>> removeTokenEvent;

  @Inject
  private DoctorRepository doctorRepository;

  /**
   * @param user
   * @return User
   * @apiNote THis methode is used to create Admin account
   */
  public User createUser(User user) {
    if (userRepository.findById(user.getUsername()).isPresent()) {
      throw new UserAlreadyExistsException(user.getUsername() + " is already exists");
    }
    user.updatePassword(user.getPassword(), argon2Utility);
    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa: ");

    System.out.println("User: "+ user);

    return userRepository.save(user);
  }

  public Stream<User> findall() {
    return userRepository.findAll();
  }

  /**
   * @param user
   * @return User
   * @throws UserAlreadyExistsException
   * @apiNote This methode is used when the admin add some users to maintain racks
   */
  @Override
  public User addUser(User user) {
    if (userRepository.findById(user.getUsername()).isPresent()) {
      throw new UserAlreadyExistsException(user.getUsername() + " already exists");
    }
    user.updatePassword(user.getPassword(), argon2Utility);
    return userRepository.save(user);
  }

  /**
   * @param username
   * @throws UserNotFoundException
   * @apiNote this methode used by the admin to delete users
   */
  @Override
  public void delete(String username) {
    if (!userRepository.findById(username).isPresent()) {
      throw new UserNotFoundException("there is  no user with username :" + username);
    }
    userRepository.deleteById(username);
  }

//  @Override
//  public User findBy(String username) {
//    return userRepository.findById(username).orElseThrow(() -> new UserNotAuthorizedException());
//  }

  @Override
  public User getLoggedUser() {

    final Principal principal = securityContext.getCallerPrincipal();
    System.out.println("Principal Name: " + (principal != null ? principal.getName() : "null"));

    if (principal == null) {
      throw new UserNotAuthorizedException();
    }
    return userRepository.findById(principal.getName())
            .orElseThrow(() -> {
              // Log when user not found
              System.out.println("User not found for principal: " + principal.getName());
              return new UserNotFoundException(principal.getName());
            });
  }

  @Override
  public void removeToken(String token) {
    final User loggedUser = getLoggedUser();
    RemoveToken<User> removeToken = new RemoveToken<>(loggedUser, token);
    removeTokenEvent.fire(removeToken);
  }

  @Override
  public User findBy(String username, String password) {
    final User user =
            userRepository.findByUsername(username).orElseThrow(UserNotAuthorizedException::new);
    System.out.println(argon2Utility.check(user.getPassword(), password.toCharArray()));
    if (argon2Utility.check(user.getPassword(), password.toCharArray())) {

      return user;
    }
    throw new UserNotAuthorizedException();
  }
  @Override
  public void addPendingDoctor(User user, String doctorUsername) {
    // Fetch the doctor by username

    // Modify the doctor's pending_patients list
    user.getPendingDoctors().add(doctorUsername); // Assuming getPendingPatients() returns a modifiable list

    // Save the changes back to the database
    userRepository.save(user);
  }
  @Override
  public JsonObject CalculateHealthStatus(HealthData healthData, SensorsData sensorsData){
    // Calculate BMI
    double heightInMeters = healthData.getHeight() / 100.0;
    double bmi = healthData.getWeight() / (heightInMeters * heightInMeters);

    // Analyze BMI
    JsonObject healthStatusJson = Json.createObjectBuilder()
            .add("bmi", bmi)
            .add("bmi_status", analyzeBMI(bmi))
            .add("blood_pressure_status", analyzeBloodPressure(sensorsData.getApHi(), sensorsData.getApLo()))
            .add("saturation_status", analyzeSaturation(sensorsData.getSaturationData()))
            .add("heart_rate_status", analyzeHeartRate(sensorsData.getHeartRateData()))
            .build();
    return (healthStatusJson);



  }
  private String analyzeHeartRate(int heartRate) {
    if (heartRate < 60) return "Very Light";
    if (heartRate < 100) return "Moderate";
    if (heartRate < 120) return "Hard";
    return "Maximum";
  }

  private String analyzeSaturation(int saturationData) {
    return (saturationData < 95) ? "Low" : "Normal";
  }

  private String analyzeBloodPressure(int apHi, int apLo) {
    if (apHi >= 140 || apLo >= 90) return "High";
    if (apHi <= 90 || apLo <= 60) return "Low";
    return "Normal";
  }

  private String analyzeBMI(double bmi) {
    if (bmi < 18.5) return "Underweight";
    if (bmi < 24.9) return "Normal";
    if (bmi < 29.9) return "Overweight";
    return "Obese";
  }







//  public void addDoctorToUser(String patientUsername, String doctorUsername) {
//    // Find the user
//    User user = userRepository.findByEmail(patientUsername)
//            .orElseThrow(() -> new UserNotFoundException(patientUsername));
//
//    // Find the doctor
//    Doctor doctor = doctorRepository.findByEmail(doctorUsername)
//            .orElseThrow(() -> new UserNotFoundException(doctorUsername));
//
//    // Add the doctor's name to the user's list
//    user.getDoctors().add(doctor.getUsername());
//
//    // Save the updated user entity
//    userRepository.save(user);
//  }
}


