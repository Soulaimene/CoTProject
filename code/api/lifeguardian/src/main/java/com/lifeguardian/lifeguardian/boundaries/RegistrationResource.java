package com.lifeguardian.lifeguardian.boundaries;

import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.HealthData;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.services.DoctorServiceImpl;
import com.lifeguardian.lifeguardian.services.UserServiceImpl;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@RequestScoped
@Path("/register")
public class RegistrationResource {
    @Inject
    private DoctorServiceImpl doctorService ;
    @Inject
    private UserServiceImpl userService ;
    @POST
    @Consumes("application/json")
    public Response registerUserOrDoctor(JsonObject json) {
        String role = json.getString("role");

        if ("Doctor".equalsIgnoreCase(role)) {
            Doctor doctor = new Doctor();
            // Populate Doctor object from JSON
            doctor.setUsername(json.getString("username"));
            doctor.setSurname(json.getString("surname"));
            doctor.setEmail(json.getString("email"));
            doctor.setPassword(json.getString("password"));
            doctorService.createDoctor(doctor);
            return Response.ok("Doctor Registered !").build();

        } else if ("User".equalsIgnoreCase(role)) {
            User user = new User();
            HealthData healthData = new HealthData();
            user.setEmail(json.getString("email"));
            user.setUsername(json.getString("username"));
            user.setSurname(json.getString("surname"));
            user.setEmergencyContactEmail(json.getString("emergencyContactEmail"));
            healthData.fromJson(json.getJsonObject("healthData"));
            user.setHealthData(healthData);
            user.setPassword(json.getString("password"));
            user.setPrediction(json.getInt("prediction"));

            userService.createUser(user);

            return Response.ok("Hello User").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid role").build();
        }

    }
}
