package com.lifeguardian.lifeguardian.models;


import com.lifeguardian.lifeguardian.utils.Argon2Utility;
import jakarta.json.bind.annotation.JsonbVisibility;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import javax.management.relation.Role;
import java.io.Serializable;
import java.util.*;

import jakarta.persistence.Embedded;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class User   implements Serializable {

    @Id
    @Column("username")
    private String username;

    @Column("surname")
    private String surname;
    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("emergencyContactEmail")
    private String emergencyContactEmail;

    @Column("pending_doctors")
    private List<String> pendingDoctors;

    @Column("doctors")
    private List<String> doctors;

    @Column("prediction")
    private int prediction;

    @Column("role")
    private final String role;

//    @Column("health_status")
//    private Map<String, Object> healthStatus;

    /**
     * Getters
     */
    @Column("healthData")
    private HealthData healthData;

    public String getUsername() {
        return username;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


    public String getEmergencyContactEmail() {
        return emergencyContactEmail;
    }

    public HealthData getHealthData() {
        return healthData;
    }

    public List<String> getPendingDoctors() {
        return pendingDoctors;
    }

    public List<String> getDoctors() {
        return doctors;
    }
    public String getRole() {
        return role;
    }


    public int getPrediction() {
        return prediction;
    }

    /**
     * Setters
     */

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmergencyContactEmail(String emergencyContactEmail) {
        this.emergencyContactEmail = emergencyContactEmail;
    }

    public void setHealthData(HealthData healthData) {
        this.healthData = healthData;
    }

    public void setPendingDoctors(List<String> pendingDoctors) {
        this.pendingDoctors = pendingDoctors;
    }

    public void setDoctors(List<String> doctors) {
        this.doctors = doctors;
    }

    public void setPrediction(int prediction) {
        this.prediction = prediction;
    }

    /**
     * All Args  constructor  and No Args  constructor
     */



    public  User(){
        this.pendingDoctors = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.role = "User";
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(surname, user.surname) &&
                Objects.equals(email, user.email);
    }

//    different values for these fields produce different hash codes
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, surname ,email );
    }

    @Override
    public String toString() {
        return "{" +
                " surname='" + surname + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", emergencyContactEmail='" + emergencyContactEmail + '\'' +
                ", healthData=" + healthData +
                ", pendingDoctors=" + pendingDoctors +
                ", doctors=" + doctors +
                ", prediction=" + prediction +
                ", role=" + role +
                '}';
    }
    public void updatePassword(String password, Argon2Utility argon2Utility) {
        this.password = argon2Utility.hash(password.toCharArray());
    }




}