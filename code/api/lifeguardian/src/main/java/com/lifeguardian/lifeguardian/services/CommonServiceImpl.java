package com.lifeguardian.lifeguardian.services;

import com.lifeguardian.lifeguardian.exceptions.InvalidCredentialsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import com.lifeguardian.lifeguardian.utils.Argon2Utility;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.util.*;

public class CommonServiceImpl implements CommonService {
    @Inject
    private UserRepository userRepository;
    @Inject private DoctorRepository doctorRepository;
    @Inject
    Argon2Utility argon2Utility;

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
    public boolean validateUser(String username, String password, String role) {
        if ("User".equalsIgnoreCase(role)) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

            if (!argon2Utility.check(user.getPassword(), password.toCharArray())) {
                throw new InvalidCredentialsException("Invalid password for username: " + username);
            }
            return true; // User exists and password matches

        } else if ("Doctor".equalsIgnoreCase(role)) {
            Doctor doctor = doctorRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Doctor not found with username: " + username));

            if (!argon2Utility.check(doctor.getPassword(), password.toCharArray())) {
                throw new InvalidCredentialsException("Invalid password for doctor: " + username);
            }
            return true; // Doctor exists and password matches

        }
        return false; // Validation failed
    }
    public String getUserRole(String username) throws UserNotFoundException {
        // Attempt to find the user in the userRepository
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return "User"; // or return userOpt.get().getRole() if roles are more specific
        }

        // If not found, attempt to find the doctor in the doctorRepository
        Optional<Doctor> doctorOpt = doctorRepository.findByUsername(username);
        if (doctorOpt.isPresent()) {
            return "Doctor"; // or return doctorOpt.get().getRole() if roles are more specific
        }

        // If neither is found, throw an exception
        throw new UserNotFoundException("User not found with username: " + username);
    }
}
