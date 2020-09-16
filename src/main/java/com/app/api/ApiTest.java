package com.app.api;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/apitest")
public class ApiTest {
@GET
@Produces(MediaType.TEXT_PLAIN)
public String getTestService() {
return "Hello World! This is coming from webservice!!";

}

}
