package com.lifeguardian.lifeguardian.repository;

import com.lifeguardian.lifeguardian.models.SensorDB;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;



import java.util.stream.Stream;
@Repository
public interface SensorDBRepository  extends CrudRepository <SensorDB, String> { // repository containing the methods for interacting with SensorDB entity in mongodb
    Stream<SensorDB> findAll();

}