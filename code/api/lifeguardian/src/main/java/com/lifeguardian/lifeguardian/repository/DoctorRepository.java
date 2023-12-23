package com.lifeguardian.lifeguardian.repository;



import com.lifeguardian.lifeguardian.models.Doctor;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, String> {

    Optional<Doctor> findByUsername(String username);

    Stream<Doctor> findAll();
}