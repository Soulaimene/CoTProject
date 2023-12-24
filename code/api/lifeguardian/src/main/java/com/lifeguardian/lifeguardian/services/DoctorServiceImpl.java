package com.lifeguardian.lifeguardian.services;

import com.lifeguardian.lifeguardian.exceptions.UserNotAuthorizedException;
import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import com.lifeguardian.lifeguardian.security.RemoveToken;
import com.lifeguardian.lifeguardian.utils.Argon2Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.core.Response;

import java.security.Principal;
import java.util.HashMap;

@ApplicationScoped

public class DoctorServiceImpl implements DoctorService {

    @Inject

    private DoctorRepository doctorRepository;
    @Inject
    Argon2Utility argon2Utility ;

    @Inject
    private SecurityContext securityContext;

    @Inject
    private Event<RemoveToken<Doctor>> removeTokenEvent;
    @Inject
    private UserRepository userRepository;

    @Override
    public Doctor createDoctor(Doctor doctor) throws UserAlreadyExistsException {
        if(doctorRepository.findById(doctor.getEmail()).isPresent()){
            throw  new UserAlreadyExistsException(doctor.getEmail()+" is already exists") ;
        }
        doctor.updatePassword(doctor.getPassword(),argon2Utility);
        return  doctorRepository.save(doctor) ;
    }

    @Override
    public Doctor addDoctor(Doctor doctor) throws UserAlreadyExistsException {
        if(doctorRepository.findById(doctor.getEmail()).isPresent()){
            throw new UserAlreadyExistsException(doctor.getEmail() +" already exists") ;
        }
        doctor.updatePassword(doctor.getPassword(),argon2Utility);
        return doctorRepository.save(doctor) ;
    }

    @Override
    public void delete(String email) throws UserNotFoundException {
        if(!doctorRepository.findById(email).isPresent()){
            throw new UserNotFoundException("there is  no user with email :"+email) ;
        }
        doctorRepository.deleteById(email);
    }

    @Override
    public Doctor getLoggedDoctor() {
        final Principal principal = securityContext.getCallerPrincipal();
        if (principal == null) {
            throw new UserNotAuthorizedException();
        }
        return doctorRepository.findById(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()));
    }

    @Override
    public void removeToken(String token) {
        final Doctor loggedDoctor = getLoggedDoctor();
        RemoveToken<Doctor> removeToken = new RemoveToken<>(loggedDoctor, token);
        removeTokenEvent.fire(removeToken);
    }
    @Override
    public Doctor findBy(String username, String password) {
        final Doctor doctor = doctorRepository.findByUsername(username)
                .orElseThrow(UserNotAuthorizedException::new);



        if (argon2Utility.check(doctor.getPassword(), password.toCharArray())) {
            return doctor;
        }
        throw new UserNotAuthorizedException();
    }
    @Override
    public void addPendingPatient(Doctor doctor, String patientUsername) {
        // Fetch the doctor by username

        // Modify the doctor's pending_patients list
        doctor.getPendingPatients().add(patientUsername); // Assuming getPendingPatients() returns a modifiable list

        // Save the changes back to the database
        doctorRepository.save(doctor);
    }
    @Override
    public Response processPendingUser(String doctorUsername, String patientUsername, String status) {
        Doctor doctor = doctorRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found with username: " + doctorUsername));

        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new UserNotFoundException("Patient not found with username: " + patientUsername));

        // Check if the patient is in the doctor's pending list
        if (!doctor.getPendingPatients().contains(patientUsername)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Patient not in pending list").build();
        }

        if ("accept".equalsIgnoreCase(status)) {
            // Add the patient to the doctor's patients list and remove from pending
            doctor.getPatients().add(patientUsername);
            doctor.getPendingPatients().remove(patientUsername);

            // Add the doctor to the user's doctors list and remove from pending
            patient.getDoctors().add(doctorUsername);
            patient.getPendingDoctors().remove(doctorUsername);

            // Persist changes to the database
            doctorRepository.save(doctor);
            userRepository.save(patient);
        } else {
            // If not accepting, just remove from pending lists
            doctor.getPendingPatients().remove(patientUsername);
            patient.getPendingDoctors().remove(doctorUsername);

            // Persist changes to the database
            doctorRepository.save(doctor);
            userRepository.save(patient);
        }

        // Return a success message
        return Response.ok(new HashMap<String, String>() {{
            put("message", "Patient " + (status.equalsIgnoreCase("accept") ? "added" : "removed") + " successfully");
        }}).build();
    }

}
