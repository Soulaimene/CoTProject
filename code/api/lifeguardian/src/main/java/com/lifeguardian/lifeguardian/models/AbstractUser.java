//package com.lifeguardian.lifeguardian.models;
//
//import com.lifeguardian.lifeguardian.utils.Argon2Utility;
//import jakarta.json.bind.annotation.JsonbVisibility;
//import jakarta.nosql.Column;
//import jakarta.nosql.Entity;
//import jakarta.nosql.Id;
//
//import java.io.Serializable;
//import java.util.Objects;
//import java.util.Set;
//
//@Entity
//@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
//public class AbstractUser implements Serializable {
//    @Id
//    @Column("username")
//    private String username;
//
//    @Column("lastname")
//    private String lastname;
//    @Column("email")
//    private String email;
//
//    @Column("password")
//    private String password;
//
//    @Column
//    private Set<Role> roles;
//
//    public AbstractUser() {
//         // Initialize the roles set
//    }
//
//
//    public String getUsername() {
//        return username;
//    }
//
//    public String getLastname() {
//        return lastname;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//    public Set<Role> getRoles() {
//        return roles;
//    }
//
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public void setLastname(String lastname) {
//        this.lastname = lastname;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public void setRoles(Set<Role> roles) {
//        this.roles = roles;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        AbstractUser abstractUser = (AbstractUser) o;
//        return Objects.equals(username, abstractUser.username) &&
//                Objects.equals(lastname, abstractUser.lastname) &&
//                Objects.equals(email, abstractUser.email);
//    }
//
//    public String toString() {
//        return "{" +
//                " surname='" + lastname + '\'' +
//                ", username='" + username + '\'' +
//                ", email='" + email + '\'' +
//                ", password='" + password + '\'' +
//                '}';
//    }
//
//    public void updatePassword(String password, Argon2Utility argon2Utility) {
//        this.password = argon2Utility.hash(password.toCharArray());
//        }
//
//
//    }
