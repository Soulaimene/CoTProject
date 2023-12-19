package com.lifeguardian.lifeguardian.boundaries;


import com.lifeguardian.lifeguardian.models.SensorDB;
import com.lifeguardian.lifeguardian.repository.SensorDBRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
@ApplicationScoped
@Path("sensor") //The path where the REST service is going to be implemented
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)  // @produces and @consumes to specifiy that the data sent and received is in JSON format
public class SensorDBEndpoint {
    private static final Supplier<WebApplicationException> NOT_FOUND =
            () -> new WebApplicationException(Response.Status.NOT_FOUND);

    @Inject
    private SensorDBRepository repository; // Inject the repository to  utilize its methods of interacting with the database
    @GET
    public List<SensorDB> findAll() { //GET METHOD to receive a list of all SensorDB data from the database
        return repository.findAll()
                .collect(toList());
    }
    @POST // POST METHOD to send the data of the sensor in JSON format and save it in the database
    public void save(SensorDB sensor) {
        repository.save(sensor);
    }

}