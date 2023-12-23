package com.lifeguardian.lifeguardian.security;



import com.lifeguardian.lifeguardian.exceptions.UserNotAuthorizedException;
import com.lifeguardian.lifeguardian.models.Doctor;
import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.repository.DoctorRepository;
import com.lifeguardian.lifeguardian.repository.UserRepository;
import com.lifeguardian.lifeguardian.services.DoctorServiceImpl;
import com.lifeguardian.lifeguardian.services.UserServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class Oauth2Service {
    static final int EXPIRE_IN = 900;
    static final Duration EXPIRES = Duration.ofSeconds(EXPIRE_IN);
    @Inject
    private UserServiceImpl userSecurityService;
    @Inject
    private DoctorServiceImpl doctorSecurityService;

    @Inject
    private UserRepository user_repository;

    @Inject
    private DoctorRepository doctor_repository;
    @Inject
    private Validator validator;
//    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//    LocalDateTime now = LocalDateTime.now();

    public Map<String, Object> token(Oauth2Request request, Class<?> userType) {

        final Set<ConstraintViolation<Oauth2Request>> violations = validator.validate(request, Oauth2Request
                .GenerateToken.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (User.class.equals(userType)) {

            User user = userSecurityService.findBy(request.getEmail(), request.getPassword());
            return generateTokenMap(user, user.getEmail(), user.getUsername());
        } else if (Doctor.class.equals(userType)) {
            Doctor doctor = doctorSecurityService.findBy(request.getEmail(), request.getPassword());
            return generateTokenMap(doctor, doctor.getEmail(), doctor.getUsername());
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }
    }

    private <T> Map<String, Object> generateTokenMap(T user, String email, String username ) {
        final Token token = Token.generate();
        final String jwt = UserJWT.createToken(user, token, EXPIRES);
        AccessToken accessToken = new AccessToken(jwt, token.get(), EXPIRES);
        RefreshToken refreshToken = new RefreshToken(Token.generate(), accessToken);
        HashMap<String, Object> map = new HashMap<>();
        map.put("accessToken", accessToken.getToken());
        map.put("refreshToken", refreshToken.getToken());
        map.put("email", email);
        map.put("fullname", username);
        return map;
    }
    public Map<String, Object>  refreshToken(Oauth2Request request, Class<?> userType) {
        System.out.println("refresh methode is activated");
        final Set<ConstraintViolation<Oauth2Request>> violations = validator.validate(request, Oauth2Request
                .RefreshToken.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        if (User.class.equals(userType)) {
            User user = userSecurityService.findBy(request.getEmail(), request.getPassword());
            return refreshTokenMap(user, user.getEmail(), user.getUsername());
        } else if (Doctor.class.equals(userType)) {
            Doctor doctor = doctorSecurityService.findBy(request.getEmail(), request.getPassword());
            return refreshTokenMap(doctor, doctor.getEmail(), doctor.getUsername());
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }
    }

    private <T> Map<String, Object> refreshTokenMap(T user, String email, String username ) {
        final Token token = Token.generate();
        final String jwt = UserJWT.createToken(user, token, EXPIRES);
        AccessToken accessToken = new AccessToken(jwt, token.get(), EXPIRES);
        RefreshToken refreshToken = new RefreshToken(Token.generate(), accessToken);
        final Oauth2Response response = Oauth2Response.of(accessToken, refreshToken, EXPIRE_IN);
        HashMap<String, Object> map = new HashMap<>();
        map.put("accessToken", response.getAccessToken());
        map.put("refreshToken", response.getRefreshToken());
        map.put("Expires", response.getExpiresIn());
        return map;
    }




}


