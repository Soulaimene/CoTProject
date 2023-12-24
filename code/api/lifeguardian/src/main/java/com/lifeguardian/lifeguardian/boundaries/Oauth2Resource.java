package com.lifeguardian.lifeguardian.boundaries;

import com.lifeguardian.lifeguardian.exceptions.InvalidCredentialsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.security.Oauth2Request;
import com.lifeguardian.lifeguardian.security.Oauth2Service;
import com.lifeguardian.lifeguardian.services.CommonServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.lifeguardian.lifeguardian.services.DoctorServiceImpl;
import com.lifeguardian.lifeguardian.services.UserServiceImpl;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
@Path("oauth2")
public class Oauth2Resource {
    @Inject
    private Oauth2Service service;
    @Inject
    private DoctorServiceImpl doctorService ;
    @Inject
    private CommonServiceImpl commonService ;
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> token(@Valid Oauth2Request request) {
        // Assuming the "role" field in the request contains the user type as a string
        String username = request.getUsername();
        String password = request.getPassword();
        String role = commonService.getUserRole(username);


        // Validate user or doctor based on the role
        commonService.validateUser(username, password, role);

        // Proceed with the logic based on the grant type
        switch (request.getGrandType()) {
            case PASSWORD:
                // Generate token if the role is User or Doctor
                if ("User".equalsIgnoreCase(role) || "Doctor".equalsIgnoreCase(role)) {
                    return service.token(request,role);
                } else {
                    throw new UnsupportedOperationException("Unsupported role: " + role);
                }
            case REFRESH_TOKEN:
                // Refresh token if the role is User or Doctor
                if ("User".equalsIgnoreCase(role) || "Doctor".equalsIgnoreCase(role)) {
                    return service.refreshToken(request,role);
                } else {
                    throw new UnsupportedOperationException("Unsupported user type");
                }
            default:
                throw new UnsupportedOperationException("There is no support for another type");
        }

    }

    // Method to map role string to user type class


}