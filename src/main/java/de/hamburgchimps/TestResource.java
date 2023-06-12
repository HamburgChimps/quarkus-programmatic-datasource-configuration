package de.hamburgchimps;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/test")
public class TestResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String test() {
        new TestEntity().persist();
        return "hey!";
    }
}