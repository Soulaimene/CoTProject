package com.lifeguardian.lifeguardian.services;


import com.lifeguardian.lifeguardian.exceptions.UserNotAuthorizedException;
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
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.security.enterprise.SecurityContext;


import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    if (userRepository.findById(user.getEmail()).isPresent()) {
      throw new UserAlreadyExistsException(user.getEmail() + " is already exists");
    }
    user.updatePassword(user.getPassword(), argon2Utility);
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
    if (userRepository.findById(user.getEmail()).isPresent()) {
      throw new UserAlreadyExistsException(user.getEmail() + " already exists");
    }
    user.updatePassword(user.getPassword(), argon2Utility);
    return userRepository.save(user);
  }

  /**
   * @param email
   * @throws UserNotFoundException
   * @apiNote this methode used by the admin to delete users
   */
  @Override
  public void delete(String email) {
    if (!userRepository.findById(email).isPresent()) {
      throw new UserNotFoundException("there is  no user with email :" + email);
    }
    userRepository.deleteById(email);
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
  public User findBy(String email, String password) {
    final User user =
        userRepository.findByEmail(email).orElseThrow(UserNotAuthorizedException::new);
    System.out.println(argon2Utility.check(user.getPassword(), password.toCharArray()));
    if (argon2Utility.check(user.getPassword(), password.toCharArray())) {

      return user;
    }
    throw new UserNotAuthorizedException();
  }

  @Override
  public Map<String, String> getCurrentUser(String token) {
    // Decode the token
    Map<String, Object> tokenInfo = decodeToken(token);

    // Extract the username and roles
    String username = (String) tokenInfo.get("jti");
    List<String> roles = (List<String>) tokenInfo.get("roles");

    // Prepare the response
    Map<String, String> response = new HashMap<>();
    response.put("username", username);
    response.put("role", roles != null && !roles.isEmpty() ? roles.get(0) : "No role");

    return response;
  }
  private Map<String, Object> decodeToken(String token) {
    // Split the token into parts
    String[] parts = token.split("\\.");
    if (parts.length != 3) {
      throw new IllegalArgumentException("Invalid token");
    }

    // Decode the payload part
    String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

    // Deserialize the JSON payload to a map
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(payload, Map.class);
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



