package com.lifeguardian.lifeguardian.boundaries;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Path("/home")
public class FrontendRouter {
    @Context
    ServletContext servletContext;

    @Path("/{path: .+}")
    @GET
    public InputStream getFile(@PathParam("path") String path) {
        try {
            String base = servletContext.getRealPath("/WEB-INF/classes/files");
            File f = new File(String.format("%s/%s", base, path));
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            // log the error?
            return null;
        }
    }
}