package de.hamburgchimps;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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