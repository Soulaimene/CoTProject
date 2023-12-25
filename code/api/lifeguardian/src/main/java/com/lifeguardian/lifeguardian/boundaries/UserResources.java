package com.lifeguardian.lifeguardian.boundaries;


import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.HealthData;
import com.lifeguardian.lifeguardian.models.SensorsData;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import com.lifeguardian.lifeguardian.services.CommonService;
import com.lifeguardian.lifeguardian.services.DoctorService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;

import com.lifeguardian.lifeguardian.services.UserServiceImpl;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResources {

    @Inject
    private UserServiceImpl userService ;

    @Inject private UserRepository userRepository;
    @Inject private DoctorRepository doctorRepository;

    @Inject
    private CommonService commonServiceImpl;
    @Inject
    private DoctorService doctorService;

    @Path("addDoctor/{doctorUsername}")
    @POST

    public Response addDoctor(@PathParam("doctorUsername") String doctorUsername, @Context HttpHeaders headers) {
        try {
            String authHeader = headers.getRequestHeader("Authorization").get(0);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                // Decode the token to get the current user's details
                Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);

                String username = currentUser.get("username");
                String role = currentUser.get("role");

                // Validate the current user's role
                if (!"User".equalsIgnoreCase(role)) {
                    return Response.status(Response.Status.FORBIDDEN).entity("Only patients can add doctors").build();
                }

                // Validate the doctor's existence
                Doctor doctor = doctorRepository.findByUsername(doctorUsername)
                        .orElseThrow(() -> new UserNotFoundException("Doctor not found with username: " + doctorUsername));
                // Check if the user already sent a request to the doctor
                if (doctor.getPendingPatients().contains(username)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("You already sent a request to doctor " + doctorUsername).build();
                }

                // Add the patient to the doctor's pending list
                doctorService.addPendingPatient(doctor, username);

                // Fetch the current user from the userRepository
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

                // Add the doctor to the user's pending_doctors list
                userService.addPendingDoctor(user, doctor.getUsername());

                // Return a success message with the doctor's username
                return Response.ok("Your request to doctor " + doctorUsername + " is pending").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
            }
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            // Log the exception here
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred").build();
        }
    }
    @Path("/getAllDoctors")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDoctors(@Context HttpHeaders headers) {
        try {
            String authHeader = headers.getRequestHeader("Authorization").get(0);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                // Decode the token to get the current user's details
                Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);
                String role = currentUser.get("role");

                // Check if the role is 'User'
                if (!"User".equalsIgnoreCase(role)) {
                    return Response.status(Response.Status.FORBIDDEN).entity("Only users can see all doctors").build();
                }

                // Retrieve all doctors as a stream, map to usernames, and collect to list
                try (Stream<Doctor> doctorStream = doctorRepository.findAll()) {
                    List<String> doctorUsernames = doctorStream.map(Doctor::getUsername)
                            .collect(Collectors.toList());
                    // Create a JSON array of pending patient usernames
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                    doctorUsernames.forEach(jsonArrayBuilder::add);
                    JsonArray jsonArray = jsonArrayBuilder.build();

                    // Return the list of doctor usernames
                    return Response.ok(jsonArray).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
            }
        } catch (Exception e) {
            // Log the exception here
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred").build();
        }
    }
//    expected response : {
//        "bmi_status": "Normal",
//            "blood_pressure_status": "Normal",
//            "saturation_status": "Low",
//            "heart_rate_status": "Moderate",
//            "bmi": 24.163265306122447
//    }
    @Path("/getHealthStatus")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHealthStatus(@Context HttpHeaders headers) {
        String authHeader = headers.getRequestHeader("Authorization").get(0);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());

            // Decode the token to get the current user's details
            Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);

            String username = currentUser.get("username");
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
            HealthData healthData = user.getHealthData();
            SensorsData sensorsData = user.getSensorsData();

            // Calculate BMI
            double heightInMeters = healthData.getHeight() / 100.0;
            double bmi = healthData.getWeight() / (heightInMeters * heightInMeters);
    
            // Analyze BMI
            JsonObject healthStatusJson = Json.createObjectBuilder()
                    .add("bmi_status", analyzeBMI(bmi))
                    .add("blood_pressure_status", analyzeBloodPressure(sensorsData.getApHi(), sensorsData.getApLo()))
                    .add("saturation_status", analyzeSaturation(sensorsData.getSaturationData()))
                    .add("heart_rate_status", analyzeHeartRate(sensorsData.getHeartRateData()))
                    .add("bmi", bmi)
                    .build();
            return Response.ok(healthStatusJson).build();
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
        }


   

    
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


}

    