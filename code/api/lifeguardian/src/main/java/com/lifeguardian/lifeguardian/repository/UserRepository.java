package com.lifeguardian.lifeguardian.repository;




import com.lifeguardian.lifeguardian.models.User;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;


import java.util.stream.Stream;
import java.util.Optional;
@Repository
public interface UserRepository extends CrudRepository<User,String> {

    Optional<User> findByUsername(String username ) ;
    Stream<User> findAll() ;

}