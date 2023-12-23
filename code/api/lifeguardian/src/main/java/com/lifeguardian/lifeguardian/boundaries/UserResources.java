package com.lifeguardian.lifeguardian.boundaries;


import com.lifeguardian.lifeguardian.models.User;
import com.lifeguardian.lifeguardian.exceptions.UserAlreadyExistsException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import com.lifeguardian.lifeguardian.exceptions.UserNotFoundException;

import com.lifeguardian.lifeguardian.services.UserServiceImpl;
import com.lifeguardian.lifeguardian.services.CommonServiceImpl;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.Map;

@ApplicationScoped
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResources {

    @Inject
    private UserServiceImpl userService ;

    @Inject
    private CommonServiceImpl commonServiceImpl ;


    /**
     *
     * @param
     * @return Response entity
     * @throws  UserAlreadyExistsException
     * @apiNote : used to  create admin account
     */

    @GET
    @Path("/find")
    @RolesAllowed("ADMIN")
    public Response findUsers(){
        System.out.println("find");
        try {
            return Response.ok(userService.findall()).build() ;
        } catch (UserAlreadyExistsException e){
            return  Response.status(400, e.getMessage()).build();
        }


    }


    /**
     *
     * @param user
     * @return Response entity
     * @throws  UserAlreadyExistsException
     * @apiNote : used to  create admin account
     */

    @POST
    @Path("/signup")
    public Response createUser(@Valid User user){
        System.out.println("signup");
        try {
            return Response.ok(userService.createUser(user)).build() ;
        } catch (UserAlreadyExistsException e){
            return  Response.status(400, e.getMessage()).build();
        }


    }

    /**
     *
     * @param user
     * @return status
     * @apiNote  this methode is used by the admin to add users
     */

    @POST()
    @Path("user/add")
    @RolesAllowed("ADMIN")
    public  Response addUser( @Valid User user){
        try {
            var createdUser = userService.addUser(user);
            return Response.ok(createdUser.getUsername() + createdUser.getSurname() + "is added successfully ").build();
        } catch(UserAlreadyExistsException e) {
            return Response.status(400 , e.getMessage()).build() ;

        }

    }


    /**
     *
     * @param email
     * @return status
     * @apiNote  this  methode is used by the Admin to delete users
     */

    @DELETE()
    @Path("user/{email}")
    @RolesAllowed("ADMIN")
    public  Response deleteUser(@PathParam("email") String email){
        try {
            userService.delete(email);
            return  Response.ok().build() ;
        }catch(UserNotFoundException e){
            return  Response.status(400 , e.getMessage()).build() ;
        }


    }

    @GET
    @Path("/current-user")
    public Response getCurrentUser(@Context HttpHeaders headers) {
        // Retrieve the Authorization header
        String authHeader = headers.getRequestHeader("Authorization").get(0);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());

            // Use userService to get the current user
            Map<String, String> currentUser = commonServiceImpl.getCurrentUser(token);

            return Response.ok(currentUser).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No valid authorization token provided").build();
        }
    }










}