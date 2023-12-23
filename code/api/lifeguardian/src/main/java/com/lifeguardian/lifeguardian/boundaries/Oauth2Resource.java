package com.lifeguardian.lifeguardian.boundaries;

import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.security.Oauth2Request;
import com.lifeguardian.lifeguardian.security.Oauth2Service;
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

import java.util.Map;

@ApplicationScoped
@Path("oauth2")
public class Oauth2Resource {
    @Inject
    private Oauth2Service service;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> token(@Valid Oauth2Request request) {
        // Assuming the "role" field in the request contains the user type as a string
        String role = request.getRole();
        Class<?> userType = mapRoleToUserType(role);

        switch (request.getGrandType()) {
            case PASSWORD:
                if (User.class.equals(userType)) {
                    return service.token(request, User.class);
                } else if (Doctor.class.equals(userType)) {
                    return service.token(request, Doctor.class);
                } else {
                    throw new UnsupportedOperationException("Unsupported user type");
                }
            case REFRESH_TOKEN:
                if (User.class.equals(userType)) {
                    return service.refreshToken(request, User.class);
                } else if (Doctor.class.equals(userType)) {
                    return service.refreshToken(request, Doctor.class);
                } else {
                    throw new UnsupportedOperationException("Unsupported user type");
                }
            default:
                throw new UnsupportedOperationException("There is no support for another type");
        }
    }

    // Method to map role string to user type class
    private Class<?> mapRoleToUserType(String role) {
        switch (role.toLowerCase()) {
            case "user":
                return User.class;
            case "doctor":
                return Doctor.class;
            // Add more cases as needed
            default:
                throw new UnsupportedOperationException("Unsupported role: " + role);
        }
    }

}