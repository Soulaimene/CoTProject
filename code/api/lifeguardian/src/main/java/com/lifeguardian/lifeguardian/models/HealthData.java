package com.lifeguardian.lifeguardian.models;

import jakarta.json.JsonObject;
import jakarta.json.bind.annotation.JsonbVisibility;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.persistence.Embeddable;


import java.io.Serializable;
import java.util.Objects;

@Entity
public class HealthData {

    @Id
    private String id;

    @Column
    private int age;

    @Column
    private int height;

    @Column
    private int weight;

    @Column
    private int gender;

    @Column
    private int cholesterol;

    @Column
    private int gluc;

    @Column
    private int smoke;

    @Column
    private int alco;

    @Column
    private int active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(int cholesterol) {
        this.cholesterol = cholesterol;
    }

    public int getGluc() {
        return gluc;
    }

    public void setGluc(int gluc) {
        this.gluc = gluc;
    }

    public int getSmoke() {
        return smoke;
    }

    public void setSmoke(int smoke) {
        this.smoke = smoke;
    }

    public int getAlco() {
        return alco;
    }

    public void setAlco(int alco) {
        this.alco = alco;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }


    // Equals and HashCode
    public void fromJson(JsonObject json) {
        if (json.containsKey("id")) {
            this.setId(json.getString("id"));
        }

        if (json.containsKey("age")) {
            this.setAge(json.getInt("age"));
        }

        if (json.containsKey("height")) {
            this.setHeight(json.getInt("height"));
        }

        if (json.containsKey("weight")) {
            this.setWeight(json.getInt("weight"));
        }

        if (json.containsKey("gender")) {
            this.setGender(json.getInt("gender"));
        }

        if (json.containsKey("cholesterol")) {
            this.setCholesterol(json.getInt("cholesterol"));
        }

        if (json.containsKey("gluc")) {
            this.setGluc(json.getInt("gluc"));
        }

        if (json.containsKey("smoke")) {
            this.setSmoke(json.getInt("smoke"));
        }

        if (json.containsKey("alco")) {
            this.setAlco(json.getInt("alco"));
        }

        if (json.containsKey("active")) {
            this.setActive(json.getInt("active"));
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthData that = (HealthData) o;
        return age == that.age &&
                height == that.height &&
                weight == that.weight &&
                gender == that.gender &&
                cholesterol == that.cholesterol &&
                gluc == that.gluc &&
                smoke == that.smoke &&
                alco == that.alco &&
                active == that.active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, height, weight, gender, cholesterol, gluc, smoke, alco, active);
    }

    // ToString

    @Override
    public String toString() {
        return "HealthData{" +

                "age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", gender=" + gender +
                ", cholesterol=" + cholesterol +
                ", gluc=" + gluc +
                ", smoke=" + smoke +
                ", alco=" + alco +
                ", active=" + active +
                '}';
    }
}
