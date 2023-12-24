package com.lifeguardian.lifeguardian.boundaries;

import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import com.lifeguardian.lifeguardian.services.CommonService;
import com.lifeguardian.lifeguardian.services.DoctorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@Path("doctor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DoctorResources {

    @Inject
    private DoctorService doctorService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private DoctorRepository doctorRepository;

    @Inject
    private CommonService commonServiceImpl;

    @Path("/getAllPendingPatients")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPendingPatients(@Context HttpHeaders headers) {
        try {
            String authHeader = headers.getRequestHeader("Authorization").get(0);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                // Decode the token to get the current user's details
                Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);
                String username = currentUser.get("username");
                String role = currentUser.get("role");

                // Validate the current user's role
                if (!"Doctor".equalsIgnoreCase(role)) {
                    return Response.status(Response.Status.FORBIDDEN).entity("Only doctors can see pending patients").build();
                }

                // Retrieve the doctor's pending patients
                Doctor doctor = doctorRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("Doctor not found with username: " + username));

                List<String> pendingPatients = doctor.getPendingPatients();
                // Create a JSON array of pending patient usernames
                JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                pendingPatients.forEach(jsonArrayBuilder::add);
                JsonArray jsonArray = jsonArrayBuilder.build();

                // Return the list of pending patient usernames
                return Response.ok(jsonArray).build();
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
    @Path("/selectPendingUser/{patientUsername}/{status}")
    @POST

    public Response selectPendingUser(@PathParam("patientUsername") String patientUsername,@PathParam("status") String status, @Context HttpHeaders headers) {
        try {
            String authHeader = headers.getRequestHeader("Authorization").get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                // Decode the token to get the current doctor's details
                Map<String, String> currentDoctor = commonServiceImpl.getCurrentUser(token);
                String doctorUsername = currentDoctor.get("username");
                String role = currentDoctor.get("role");

                // Check if the current user is a doctor
                if (!"Doctor".equalsIgnoreCase(role)) {
                    return Response.status(Response.Status.FORBIDDEN).entity("Only doctors can select pending users").build();
                }


                // Validate and process the request
                return doctorService.processPendingUser(doctorUsername, patientUsername, status);
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
            }
        }catch (UserNotFoundException e) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            // Log the exception here
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred").build();
        }
    }
}
