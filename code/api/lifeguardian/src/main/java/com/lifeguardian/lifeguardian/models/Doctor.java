package com.lifeguardian.lifeguardian.models;

import com.lifeguardian.lifeguardian.utils.Argon2Utility;
import jakarta.json.bind.annotation.JsonbVisibility;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class Doctor implements Serializable {

    @Id
    @Column("username")
    private String username;

    @Column("surname")
    private String surname;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("pending_patients")
    private List<String> pendingPatients;

    @Column("patients")
    private List<String> patients;

    @Column("role")
    private final String role;

    /**
     * Getters
     */
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


    public String getRole() {
        return role;
    }

    public List<String> getPendingPatients() {
        return pendingPatients;
    }

    public List<String> getPatients() {
        return patients;
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

    public void setPendingPatients(List<String> pendingPatients) {
        this.pendingPatients = pendingPatients;
    }

    public void setPatients(List<String> patients) {
        this.patients = patients;
    }



    /**
     * All Args constructor and No Args constructor
     */
    public Doctor() {
        this.pendingPatients = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.role = "Doctor";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(username, doctor.username) &&
                Objects.equals(surname, doctor.surname) &&
                Objects.equals(email, doctor.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, surname, email);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "username='" + username + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", pendingPatients=" + pendingPatients +
                ", patients=" + patients +
                '}';
    }

    public void updatePassword(String password, Argon2Utility argon2Utility) {
        this.password = argon2Utility.hash(password.toCharArray());
    }
}
