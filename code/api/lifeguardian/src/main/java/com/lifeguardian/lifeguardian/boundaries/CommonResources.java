package com.lifeguardian.lifeguardian.boundaries;

import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import com.lifeguardian.lifeguardian.security.Oauth2Request;
import com.lifeguardian.lifeguardian.services.DoctorServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.lifeguardian.lifeguardian.services.UserServiceImpl;

import java.util.Map;
import java.util.Optional;

import com.lifeguardian.lifeguardian.services.CommonServiceImpl;
@ApplicationScoped
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommonResources {
    @Inject
    private CommonServiceImpl commonServiceImpl ;

    @Inject
    private UserRepository userRepository;
    @Inject
    private DoctorRepository doctorRepository;
    @GET
    @Path("/current-user")
    public Response getCurrentUser(@Context HttpHeaders headers) {
        // Retrieve the Authorization header
        String authHeader = headers.getRequestHeader("Authorization").get(0);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());

            // Use userService to get the current user
            Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);

            return Response.ok(currentUser).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
        }
    }
    @Path("/me")
    @GET
    public Response getUserMe(@Context HttpHeaders headers) {
            String authHeader = headers.getRequestHeader("Authorization").get(0);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                // Use userService to get the current user
                Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);

                // Get the current user's username and role
                String username = (String) currentUser.get("username");
                String role = (String) currentUser.get("role");

                // Fetch and return user or doctor info based on the role
                if ("User".equalsIgnoreCase(role)) {
                    Optional<User> user = userRepository.findByUsername(username);
                    return Response.ok(user).build(); // Customize as needed
                } else if ("Doctor".equalsIgnoreCase(role)) {
                    Optional<Doctor> doctor = doctorRepository.findByUsername(username);
                    return Response.ok(doctor).build(); // Customize as needed
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid role").build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
            }



    }
}
