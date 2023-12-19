package com.lifeguardian.lifeguardian.models;


import com.lifeguardian.lifeguardian.utils.Argon2Utility;
import jakarta.json.bind.annotation.JsonbVisibility;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import javax.management.relation.Role;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class User   implements Serializable {

    @Id
    @Column("email")
    private  String email ;
    @Column( "surname")
    private  String surname ;
    @Column( "forename")

    private  String forname ;


    @Column("password" )
    private String password ;


    @Column("role")
    private Set<Role> roles ;


    /**
     *
     * All Getters
     */

    public   String getSurname(){
        return  surname ;
    }

    public   String getForname(){
        return  forname ;
    }

    public   String getEmail(){
        return  email ;
    }

    public   String getPassword(){
        return password ;
    }





    public Set<Role> getRoles() {
        return roles;
    }


    public User(String surname, String forname, String email, String password, Set<Role> roles) {
        this.surname = surname;
        this.forname = forname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    /**
     *
     * ALl Setters
     */

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setSurname(String surname){
        this.surname=surname ;
    }

    public void setForname(String forname){
        this.forname=forname ;
    }

    public   void setEmail(String email){
        this.email=email ;
    }
    public   void setPassword(String password){
        this.password=password ;
    }


    /**
     * All Args  constructor  and No Args  constructor
     */



    public  User(){

    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return forname.equals(user.forname) && surname.equals(user.surname) && email.equals(user.email) ;
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), forname, surname ,email );
    }

    @Override
    public String toString() {
        return "{" +
                " surname='" + surname + '\'' +
                ", forname='" + forname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + roles +
                '}';
    }

    public void updatePassword(String password, Argon2Utility argon2Utility) {
        this.password = argon2Utility.hash(password.toCharArray());
    }




}