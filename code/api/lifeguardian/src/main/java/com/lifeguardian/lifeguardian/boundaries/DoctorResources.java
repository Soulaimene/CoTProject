package com.lifeguardian.lifeguardian.boundaries;

import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.HealthData;
import com.lifeguardian.lifeguardian.models.SensorsData;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import com.lifeguardian.lifeguardian.services.CommonService;
import com.lifeguardian.lifeguardian.services.DoctorService;
import com.lifeguardian.lifeguardian.services.UserServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.*;
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
    private CommonService commonService;
    @Inject
    private UserServiceImpl userService;

    @Path("/getAllPendingPatients")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPendingPatients(@Context HttpHeaders headers) {
        try {
            String authHeader = headers.getRequestHeader("Authorization").get(0);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                // Decode the token to get the current user's details
                Map<String, String> currentUser = commonService.getCurrentUser(token);
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
                Map<String, String> currentDoctor = commonService.getCurrentUser(token);
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

//    Response Template
//    {
//        "health_data": {
//        "age": "70",
//                "height": "175",
//                "weight": "74",
//                "gender": "0",
//                "cholesterol": "180",
//                "gluc": "90",
//                "smoke": "0",
//                "alco": "0",
//                "active": "1"
//    },
//        "sensor_data": {
//        "ap_hi": "0",
//                "ap_lo": "0",
//                "saturationData": "0",
//                "heartRateData": "0",
//                "temp": "0"
//    },
//        "health_status": {
//        "bmi": 24.163265306122447,
//                "bmi_status": "Normal",
//                "blood_pressure_status": "Low",
//                "saturation_status": "Low",
//                "heart_rate_status": "Very Light"
//    }
//    }
    @Path("/getPatientInfo/{patientUsername}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHealthStatus(@Context HttpHeaders headers,@PathParam("patientUsername") String patientUsername) {
        String authHeader = headers.getRequestHeader("Authorization").get(0);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());

            // Decode the token to get the current user's details
            Map<String, String> currentUser = commonService.getCurrentUser(token);

            String doctorUsername = currentUser.get("username");
            Doctor doctor = doctorRepository.findByUsername(doctorUsername)
                    .orElseThrow(() -> new UserNotFoundException("Doctor not found with username: " + doctorUsername));
            User user = userRepository.findByUsername(patientUsername)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + patientUsername));
            HealthData healthData = user.getHealthData();
            SensorsData sensorsData = user.getSensorsData();
            JsonObject healthStatus= (JsonObject) userService.CalculateHealthStatus(healthData,sensorsData);
            JsonObject userInfo = Json.createObjectBuilder()
                    .add("health_data", commonService.stringToJson(healthData.toString()) /* Convert healthData to JsonObject */)
                    .add("sensor_data", commonService.stringToJson(sensorsData.toString())/* Convert sensorsData to JsonObject */)
                    .add("health_status", healthStatus)
                    .add("health_status", healthStatus)
                    .build();

            return Response.ok(userInfo).build();
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
        }





    }
}
