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
import com.lifeguardian.lifeguardian.utils.EmailSender;
import jakarta.json.*;
import jakarta.mail.MessagingException;
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

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
    @Inject
    private EmailSender emailSender;

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
    private String getCurrentTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
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
            return Response.ok(userService.CalculateHealthStatus(healthData, sensorsData)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
        }
    }
    @POST
    @Path("/predict")
    public Response predictHeartAttack(@Context HttpHeaders headers) {
        String authHeader = headers.getRequestHeader("Authorization").get(0);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);
            String username = currentUser.get("username");

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
            HealthData healthData = user.getHealthData();
            SensorsData sensorsData = user.getSensorsData();

            // Call the Python script
            try {

                String pythonPath = System.getenv("Python_venv_path");
                String CotProjectPath = System.getenv("CotProjectPath");


                System.out.println("Python path: " +pythonPath);

                String jsonString = constructPredictionJson(healthData, sensorsData).replace("\"", "\\\"");
                System.out.println("Prediction JSON String Data: " +jsonString);



                ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, CotProjectPath+"./CoTProject/code/mlops/predict.py", "\"" + jsonString + "\"");

                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                StringBuilder output = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                StringBuilder errorOutput = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }

                process.waitFor();
                // Parse the output to Integer
                Integer prediction = Integer.parseInt(output.toString().trim());
                user.setPrediction(prediction);
                userRepository.save(user);

//                    String combinedOutput = "Prediction: " + prediction + "\nErrors:\n" + errorOutput.toString();
                return Response.ok(user.getPrediction()).build();

            } catch (Exception e) {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in prediction").build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
        }
    }

    private String constructPredictionJson(HealthData healthData, SensorsData sensorsData) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("age", healthData.getAge());
        builder.add("height", healthData.getHeight());
        builder.add("weight", healthData.getWeight());
        builder.add("gender", healthData.getGender()); // Assuming gender is stored as an integer
        builder.add("ap_hi", sensorsData.getApHi());
        builder.add("ap_lo", sensorsData.getApLo());
        builder.add("cholesterol", healthData.getCholesterol());
        builder.add("gluc", healthData.getGluc());
        builder.add("smoke", healthData.getSmoke()); // Assuming smoke is a boolean
        builder.add("alco", healthData.getAlco()); // Assuming alco is a boolean
        builder.add("active", healthData.getActive() ); // Assuming active is a boolean

        JsonObject predictionDataJson = builder.build();
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(predictionDataJson);
        }

        return stringWriter.toString();
    }

    @POST
    @Path("/send-email/{lat}/{lon}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response SendEmail(@Context HttpHeaders headers,
             @PathParam("lat") String latitude,
             @PathParam("lon") String longitude) throws MessagingException {
        // Check if the Authorization header is present
        List<String> authHeaderList = headers.getRequestHeader("Authorization");
        if (authHeaderList == null || authHeaderList.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authorization header is missing").build();
        }
        String authHeader = headers.getRequestHeader("Authorization").get(0);

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring("Bearer".length());

            // Decode the token to get the current user's details
            Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);

            String username = currentUser.get("username");
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

            // Check if the user has an emergency contact
            if (user.getEmergencyContactEmail() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No emergency contact set for the user").build();
            }

            // Get the email of the emergency contact
            String emergencyContactEmail = user.getEmergencyContactEmail();
            String googleMapsLink = "https://www.google.com/maps?q=" + latitude + "," + longitude;

            // Send the emergency email using your EmailSender class
            String emergencyEmailBody = "Dear Emergency Contact,\n\n" +
                    "This is to inform you that '" + username + "' is currently facing an emergency situation and requires immediate assistance. Please take note of the following details:\n\n" +
                    "Timestamp: " + getCurrentTimestamp() + "\n" +
                    "User's Location: " + googleMapsLink + "\n\n" +
                    "Action Required:\n" +
                    "- If you are in close proximity, please provide immediate assistance.\n" +
                    "- If you are unable to respond directly, please contact the local authorities or emergency services immediately.\n\n" +
                    "Please act promptly to ensure the safety and well-being of " + username + ".\n\n" +
                    "Stay Safe,\n\n" +
                    "Life Guardian Emergency Team";

            // Send the emergency email using your EmailSender class
            String emergencySubject = "Emergency Subject: " + "Alert ! - Life Guardian";
            emailSender.sendEmail(emergencyContactEmail, emergencySubject, emergencyEmailBody);


            return Response.ok("Emergency email sent successfully to " + emergencyContactEmail).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
        }

    }
}





    