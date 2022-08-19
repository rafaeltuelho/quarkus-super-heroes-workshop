package io.quarkus.workshop.superheroes.fight.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/villains")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient
public interface VillainProxy {

    @GET
    @Path("/random")
    Villain findRandomVillain();
}