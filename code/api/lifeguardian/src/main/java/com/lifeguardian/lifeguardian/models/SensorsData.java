package com.lifeguardian.lifeguardian.models;

import jakarta.json.JsonObject;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.ArrayList;
import java.util.Objects;


@Entity
public class SensorsData {
    public  SensorsData(){
        this.apHi = 0;
        this.apLo = 0;
        this.saturationData = 0;
        this.temp = 0;
        this.heartRateData = 0;
    }
    @Id
    private String id;

    @Column
    private int apHi;  // Systolic blood pressure

    @Column
    private int apLo;  // Diastolic blood pressure

    @Column
    private int saturationData;  // Oxygen saturation

    @Column
    private int heartRateData;  // Heart rate

    @Column
    private int temp;  // Temperature

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getApHi() {
        return apHi;
    }

    public void setApHi(int apHi) {
        this.apHi = apHi;
    }

    public int getApLo() {
        return apLo;
    }

    public void setApLo(int apLo) {
        this.apLo = apLo;
    }

    public int getSaturationData() {
        return saturationData;
    }

    public void setSaturationData(int saturationData) {
        this.saturationData = saturationData;
    }

    public int getHeartRateData() {
        return heartRateData;
    }

    public void setHeartRateData(int heartRateData) {
        this.heartRateData = heartRateData;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
    public void fromJson(JsonObject json) {
        if (json.containsKey("id")) {
            this.setId(json.getString("id"));
        }

        if (json.containsKey("ap_hi")) {
            this.setApHi(json.getInt("ap_hi"));
        }

        if (json.containsKey("ap_lo")) {
            this.setApLo(json.getInt("ap_lo"));
        }

        if (json.containsKey("saturation_data")) {
            this.setSaturationData(json.getInt("saturation_data"));
        }

        if (json.containsKey("heart_rate_data")) {
            this.setHeartRateData(json.getInt("heart_rate_data"));
        }

        if (json.containsKey("temp")) {
            this.setTemp(json.getInt("temp"));
        }
    }
    // Equals, hashCode, and toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorsData that = (SensorsData) o;
        return apHi == that.apHi &&
                apLo == that.apLo &&
                saturationData == that.saturationData &&
                heartRateData == that.heartRateData &&
                temp == that.temp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(apHi, apLo, saturationData, heartRateData, temp);
    }

    @Override
    public String toString() {
        return "SensorsData{" +
                " ap_hi=" + apHi +
                ", ap_lo=" + apLo +
                ", saturationData=" + saturationData +
                ", heartRateData=" + heartRateData +
                ", temp=" + temp +
                '}';
    }
}
