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
import jakarta.json.*;
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


import java.util.*;
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

                // Validate the doctor's existence
                Doctor doctor = doctorRepository.findByUsername(doctorUsername)
                        .orElseThrow(() -> new UserNotFoundException("Doctor not found with username: " + doctorUsername));
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
                // Check if the user already sent a request to the doctor
                if (doctor.getPendingPatients().contains(username)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("You already sent a request to doctor " + doctorUsername).build();
                }

                if (user.getDoctors().contains(doctorUsername)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("This Doctor is already in your doctor list " + doctorUsername).build();
                }

                // Add the patient to the doctor's pending list
                doctorService.addPendingPatient(doctor, username);

                // Fetch the current user from the userRepository


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
    //    Response example :
//    {
//        "MyDoctors": [
//        "sou"
//    ],
//        "MyDoctorsPending": [
//        "karmoussa",
//    ]
//    }
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
                String username = currentUser.get("username");

                // Check if the role is 'User'

                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

                // Retrieve all doctors as a stream, map to usernames, and collect to list
                try (Stream<Doctor> doctorStream = doctorRepository.findAll()) {
                    List<String> doctorUsernames = doctorStream.map(Doctor::getUsername)
                            .collect(Collectors.toList());
                    List<String> myDoctors = user.getDoctors();
                    // Create a Set from myDoctors for efficient set difference operation
                    Set<String> myDoctorsSet = new HashSet<>(myDoctors);

                    // Use Java Streams to find the available doctors (doctor usernames - my doctors)
                    List<String> availableDoctors = doctorUsernames.stream()
                            .filter(doctorUsername -> !myDoctorsSet.contains(doctorUsername))
                            .collect(Collectors.toList());


                    // Create a JSON array of pending patient usernames
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                    availableDoctors.forEach(jsonArrayBuilder::add);
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
    @Path("/getMyDoctors")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyDoctors(@Context HttpHeaders headers) {
        try {
            String authHeader = headers.getRequestHeader("Authorization").get(0);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                // Decode the token to get the current user's details
                Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);
                String username = currentUser.get("username");
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
                List<String> myDoctors = user.getDoctors();
                List<String> myDoctorsPending = user.getPendingDoctors();



// Create JSON response using JsonObjectBuilder and JsonArrayBuilder
                JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
                JsonArrayBuilder myDoctorsArrayBuilder = Json.createArrayBuilder(myDoctors);
                JsonArrayBuilder myDoctorsPendingArrayBuilder = Json.createArrayBuilder(myDoctorsPending);

                responseBuilder.add("MyDoctors", myDoctorsArrayBuilder);
                responseBuilder.add("MyDoctorsPending", myDoctorsPendingArrayBuilder);
                JsonObject responseData = responseBuilder.build();


                return Response.ok(responseData).build();

                // Retrieve all doctors as a stream, map to usernames, and collect to list

            }
            else {
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
    @GET
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
            return Response.ok(userService.CalculateHealthStatus(healthData,sensorsData)).build();
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
        }








    }



}

    