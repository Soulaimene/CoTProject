package com.lifeguardian.lifeguardian.services;

import com.lifeguardian.lifeguardian.exceptions.UserNotAuthorizedException;
import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;
import com.lifeguardian.lifeguardian.security.RemoveToken;
import com.lifeguardian.lifeguardian.utils.Argon2Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;

import java.security.Principal;

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
    public Doctor findBy(String username) {
        return doctorRepository.findById(username)
                .orElseThrow(() -> new UserNotAuthorizedException());
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

        String hashedPassword = argon2Utility.hash(password.toCharArray());

        if (hashedPassword.equals(doctor.getPassword())) {
            return doctor;
        }
        throw new UserNotAuthorizedException();
    }
}
